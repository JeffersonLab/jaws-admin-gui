package org.jlab.jaws.business.session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.OverrideConsumer;
import org.jlab.jaws.clients.OverrideProducer;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.entity.AlarmOverride;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;

/**
 * @author ryans
 */
@Singleton
@Startup
@RunAs("jaws-admin")
public class KafkaOverrideFacade {
  private static final Logger LOG = Logger.getLogger(KafkaOverrideFacade.class.getName());

  private OverrideConsumer consumer = null;

  @EJB AlarmFacade alarmFacade;
  @EJB OverrideFacade overrideFacade;
  @EJB ServerStatus status;

  @PostConstruct
  private void init() {
    if (System.getenv("SKIP_OVERRIDE_KAFKA_CONNECT") == null) {
      LOG.log(Level.WARNING, "Sending and monitoring Kafka for overrides");
      populateSelectOverrides();

      overrideFacade.clearCache();

      final Properties props = KafkaConfig.getConsumerPropsWithRegistry(-1, false);
      consumer = new OverrideConsumer(props);
      EventSourceListener<AlarmOverrideKey, AlarmOverrideUnion> listener = new OverrideListener<>();
      consumer.addListener(listener);
      consumer.start();
    } else {
      LOG.log(Level.WARNING, "Skipping sending and monitoring Kafka for overrides");
    }

    status.setOverridesSent();
  }

  private void populateSelectOverrides() {
    List<OverriddenAlarmType> typeList = new ArrayList<>();

    typeList.add(OverriddenAlarmType.Disabled);
    typeList.add(OverriddenAlarmType.Filtered);
    typeList.add(OverriddenAlarmType.Shelved);

    List<AlarmOverride> list =
        overrideFacade.filterList(
            typeList, null, null, null, null, null, null, 0, Integer.MAX_VALUE);

    if (list != null && list.size() > 0) {
      try (OverrideProducer producer =
          new OverrideProducer(KafkaConfig.getProducerPropsWithRegistry())) {
        for (AlarmOverride override : list) {

          String name = override.getOverridePK().getName();
          OverriddenAlarmType type = override.getOverridePK().getType();

          AlarmOverrideKey key = new AlarmOverrideKey(name, type);
          AlarmOverrideUnion value = new AlarmOverrideUnion();

          switch (type) {
            case Disabled:
              value.setUnion(new DisabledOverride(override.getComments()));
              break;
            case Filtered:
              value.setUnion(new FilteredOverride(override.getComments()));
              break;
            case Masked:
              value.setUnion(new MaskedOverride());
              break;
            case OnDelayed:
              value.setUnion(new OnDelayedOverride());
              break;
            case OffDelayed:
              value.setUnion(new OffDelayedOverride());
              break;
            case Shelved:
              Long expirationLong = override.getExpiration().getTime();
              ShelvedReason reason = null;
              if (override.getShelvedReason() != null) {
                reason = ShelvedReason.valueOf(override.getShelvedReason());
              }
              value.setUnion(
                  new ShelvedOverride(
                      override.isOneshot(), expirationLong, reason, override.getComments()));
              break;
            case Latched:
              value.setUnion(new LatchedOverride());
              break;
          }

          producer.send(key, value);
        }
      }
    }
  }

  @PreDestroy
  private void cleanup() {
    if (consumer != null) {
      consumer.close();
    }
  }

  class OverrideListener<K, V>
      implements EventSourceListener<AlarmOverrideKey, AlarmOverrideUnion> {
    @Override
    public void batch(
        List<EventSourceRecord<AlarmOverrideKey, AlarmOverrideUnion>> records,
        boolean highWaterReached) {
      try {
        overrideFacade.oracleMerge(records);
      } catch (SQLException e) {
        LOG.log(Level.SEVERE, "Unable to merge Kafka notifications into Oracle", e);
      }
    }
  }
}
