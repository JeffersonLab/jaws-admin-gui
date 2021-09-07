package org.jlab.jaws;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.jlab.jaws.entity.RegisteredAlarm;
import org.jlab.jaws.entity.RegisteredClass;
import org.jlab.jaws.eventsource.EventSourceConfig;
import org.jlab.jaws.eventsource.EventSourceListener;
import org.jlab.jaws.eventsource.EventSourceRecord;
import org.jlab.jaws.eventsource.EventSourceTable;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Path("/sse")
@WebListener
public class SSE implements ServletContextListener {
    // max 10 concurrent users
    private ExecutorService exec = Executors.newFixedThreadPool(10);
    private Sse sse;

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.err.println("Attempting to stop executor service");

        exec.shutdownNow();

        try {
            exec.awaitTermination(8, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Timeout while awaiting shutdown");
            e.printStackTrace();
        }
    }

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context final SseEventSink sink) {
        System.err.println("Proxy connected");

        exec.execute(new Runnable() {

            @Override
            public void run() {
                final String servers = "localhost:9094";
                final String registry = "http://localhost:8081";

                final Properties registrationProps = getRegistrationProps(servers, registry);
                final Properties classProps = getClassProps(servers, registry);

                try (
                        EventSourceTable<String, RegisteredAlarm> registrationTable = new EventSourceTable<>(registrationProps);
                        EventSourceTable<String, RegisteredClass> classTable = new EventSourceTable<>(classProps);
                ) {

                    registrationTable.addListener(new EventSourceListener<String, RegisteredAlarm>() {
                        @Override
                        public void initialState(Set<EventSourceRecord<String, RegisteredAlarm>> records) {
                            sendRegistrationRecords(sink, records);
                        }

                        @Override
                        public void changes(List<EventSourceRecord<String, RegisteredAlarm>> records) {
                            sendRegistrationRecords(sink, records);
                        }

                    });

                    classTable.addListener(new EventSourceListener<String, RegisteredClass>() {
                        @Override
                        public void initialState(Set<EventSourceRecord<String, RegisteredClass>> records) {
                            sendClassRecords(sink, records);
                        }

                        @Override
                        public void changes(List<EventSourceRecord<String, RegisteredClass>> records) {
                            sendClassRecords(sink, records);
                        }

                    });

                    registrationTable.start();
                    classTable.start();


                    try {
                        while (!sink.isClosed()) {
                            sink.send(sse.newEvent("ping", ":"));  // Actively check for connection
                            System.err.println("Looping checking for disconnect");
                            Thread.sleep(5000);
                        }
                    } catch (InterruptedException e) {
                        System.err.println("SSE Thread interrupted, shutting down");
                        e.printStackTrace();
                    }
                }

                System.err.println("Proxy disconnected");
            }
        });
    }

    private Properties getRegistrationProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<RegisteredAlarm> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-proxy-registered-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "registered-alarms");
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, servers);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, VALUE_SERDE.deserializer().getClass().getName());

        // Deserializer specific configs
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        return props;
    }

    private Properties getClassProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<RegisteredClass> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-proxy-class-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "registered-classes");
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, servers);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, VALUE_SERDE.deserializer().getClass().getName());

        // Deserializer specific configs
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        return props;
    }

    private void sendRegistrationRecords(SseEventSink sink, Collection<EventSourceRecord<String, RegisteredAlarm>> records) {
        for (EventSourceRecord<String, RegisteredAlarm> record : records) {
            String key = record.getKey();
            RegisteredAlarm value = record.getValue();

            String jsonValue = null;

            if (value != null) {
                try {
                    SpecificDatumWriter<RegisteredAlarm> writer = new SpecificDatumWriter<>(value.getSchema());
                    OutputStream out = new ByteArrayOutputStream();
                    // See: https://issues.apache.org/jira/browse/AVRO-1582 - JSON encoded union fields needed to indicate nullable are encoded as array!
                    JsonEncoder encoder = EncoderFactory.get().jsonEncoder(value.getSchema(), out);
                    writer.write(value, encoder);
                    encoder.flush();
                    jsonValue = out.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + key + "\", \"value\": " + jsonValue + "}";

            sink.send(sse.newEvent("registration", recordString));
        }
    }

    private void sendClassRecords(SseEventSink sink, Collection<EventSourceRecord<String, RegisteredClass>> records) {
        for (EventSourceRecord<String, RegisteredClass> record : records) {
            String key = record.getKey();
            RegisteredClass value = record.getValue();

            String jsonValue = null;

            if (value != null) {
                try {
                    SpecificDatumWriter<RegisteredClass> writer = new SpecificDatumWriter<>(value.getSchema());
                    OutputStream out = new ByteArrayOutputStream();
                    JsonEncoder encoder = EncoderFactory.get().jsonEncoder(value.getSchema(), out);
                    writer.write(value, encoder);
                    encoder.flush();
                    jsonValue = out.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + key + "\", \"value\": " + jsonValue + "}";

            sink.send(sse.newEvent("class", recordString));
        }
    }
}