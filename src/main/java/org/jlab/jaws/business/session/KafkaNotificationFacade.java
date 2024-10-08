package org.jlab.jaws.business.session;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.jlab.jaws.business.service.BatchNotificationService;
import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.EffectiveNotificationConsumer;
import org.jlab.jaws.entity.EffectiveNotification;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;

/**
 * @author ryans
 */
@Singleton
@Startup
@RunAs("jaws-admin")
public class KafkaNotificationFacade {
  private static final Logger LOG = Logger.getLogger(KafkaNotificationFacade.class.getName());

  private EffectiveNotificationConsumer notificationConsumer = null;

  @EJB AlarmFacade alarmFacade;
  @EJB NotificationFacade notificationFacade;

  @PostConstruct
  private void init() {
    if (System.getenv("SKIP_NOTIFICATION_MERGE") == null) {
      LOG.log(Level.WARNING, "Monitoring Kafka for notifications");
      notificationFacade.clearCache();

      final Properties notificationProps = KafkaConfig.getConsumerPropsWithRegistry(-1, false);
      notificationConsumer = new EffectiveNotificationConsumer(notificationProps);
      EventSourceListener<String, EffectiveNotification> notificationListener =
          new NotificationListener<>();
      notificationConsumer.addListener(notificationListener);
      notificationConsumer.start();
    } else {
      LOG.log(Level.WARNING, "Skipping monitoring Kafka for notifications");
    }
  }

  @PreDestroy
  private void cleanup() {
    if (notificationConsumer != null) {
      notificationConsumer.close();
    }
  }

  class NotificationListener<K, V> implements EventSourceListener<String, EffectiveNotification> {
    @Override
    public void batch(
        List<EventSourceRecord<String, EffectiveNotification>> records, boolean highWaterReached) {
      try {
        long start = System.currentTimeMillis();
        notificationFacade.oracleMerge(records);
        long end = System.currentTimeMillis();

        // TODO: Consider moving history updates to a separate thread to avoid blocking notification
        // merge
        long hStart = System.currentTimeMillis();
        BatchNotificationService service = new BatchNotificationService();
        service.oracleInsertNotificationHistory(records);
        service.oracleMergeActiveHistory(records);
        service.oracleMergeSuppressedHistory(records);
        long hEnd = System.currentTimeMillis();

        LOG.log(
            Level.INFO,
            "Merged {0} batch notifications in {1} milliseconds, merged history in {2} milliseconds",
            new Object[] {records.size(), end - start, hEnd - hStart});

      } catch (SQLException e) {
        LOG.log(Level.SEVERE, "Unable to merge Kafka notifications into Oracle", e);
      }
    }
  }

  /**
   * Wait until the listed alarms receive a notification.
   *
   * <p>It is useful when applying overrides to wait to see them take effect.
   *
   * <p>Note: this does NOT guarantee that the changes that the caller made beforehand are the ones
   * we're now being notified about. This is a simple way to introduce a delay to wait for changes
   * to propagate, which is smarter than a simple sleep call, but dumber than examining results.
   *
   * <p>Note: Some overrides do not result in new messages and therefore will time out. For example
   * an acknowledgement on an alarm that isn't Latched (or sending unset messages for any Overrides
   * that are not set, or perhaps set messages for Overrides already set).
   *
   * @param nameSet The set of alarm names
   */
  @PermitAll
  public WaitForNotificationListener createWaitFor(HashSet<String> nameSet) {
    return new WaitForNotificationListener(nameSet);
  }

  public class WaitForNotificationListener
      implements EventSourceListener<String, EffectiveNotification>, AutoCloseable {
    private final HashSet<String> nameSet;
    private final CompletableFuture<Void> future = new CompletableFuture<>();

    private WaitForNotificationListener(HashSet<String> nameSet) {
      this.nameSet = new HashSet<>(nameSet);
      notificationConsumer.addListener(this);
    }

    @Override
    public void batch(
        List<EventSourceRecord<String, EffectiveNotification>> records, boolean highWaterReached) {
      for (EventSourceRecord<String, EffectiveNotification> record : records) {
        nameSet.remove(record.getKey());
      }

      if (nameSet.isEmpty()) {
        future.complete(null);
      }
    }

    /**
     * Wait for the notifications to be received (block).
     *
     * @return true if notifications were received, false if Timeout or Exception
     */
    public boolean await() {
      boolean wasNotified = true;
      try {
        future.get(15, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        wasNotified = false;
      }

      return wasNotified;
    }

    @Override
    public void close() {
      notificationConsumer.removeListener(this);
    }
  }
}
