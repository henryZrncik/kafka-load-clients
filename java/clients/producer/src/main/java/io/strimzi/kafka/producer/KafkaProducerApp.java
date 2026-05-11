package io.strimzi.kafka.producer;

import io.strimzi.kafka.clients.ClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaProducerApp {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerApp.class);

    public static void main(String[] args) {
        final String topic = System.getenv().getOrDefault("TOPIC", "test-topic");
        final int messageCount = Integer.parseInt(System.getenv().getOrDefault("MESSAGE_COUNT", "1000"));
        final int delayMs = Integer.parseInt(System.getenv().getOrDefault("DELAY_MS", "1000"));

        log.info("Starting Kafka Producer");
        log.info("Topic: {}", topic);
        log.info("Message count: {}", messageCount);
        log.info("Delay: {} ms", delayMs);

        Properties props = ClientConfig.createProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            int successCount = 0;
            int errorCount = 0;

            for (int i = 0; i < messageCount; i++) {
                String key = "key-" + i;
                String value = String.format("{\"message\": \"Message %d\", \"timestamp\": \"%s\"}",
                    i, Instant.now().toString());

                ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

                try {
                    RecordMetadata metadata = producer.send(record).get();
                    successCount++;
                    log.info("Sent message {} to partition {} with offset {}",
                        i, metadata.partition(), metadata.offset());
                } catch (InterruptedException | ExecutionException e) {
                    errorCount++;
                    log.error("Error sending message {}: {}", i, e.getMessage());
                }

                if (delayMs > 0 && i < messageCount - 1) {
                    Thread.sleep(delayMs);
                }
            }

            log.info("Producer finished. Sent: {}, Errors: {}", successCount, errorCount);
        } catch (Exception e) {
            log.error("Producer error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
