package io.strimzi.kafka.consumer;

import io.strimzi.kafka.clients.ClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerApp {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerApp.class);

    public static void main(String[] args) {
        String topic = System.getenv().getOrDefault("TOPIC", "test-topic");
        String groupId = System.getenv().getOrDefault("GROUP_ID", "test-consumer-group");

        log.info("Starting Kafka Consumer");
        log.info("Topic: {}", topic);
        log.info("Group ID: {}", groupId);

        Properties props = ClientConfig.createProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            log.info("Subscribed to topic: {}", topic);

            long messageCount = 0;

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    messageCount++;
                    log.info("Received message #{} - Partition: {}, Offset: {}, Key: {}, Value: {}",
                        messageCount, record.partition(), record.offset(), record.key(), record.value());
                }

                if (messageCount > 0 && messageCount % 100 == 0) {
                    log.info("Total messages consumed: {}", messageCount);
                }
            }
        } catch (Exception e) {
            log.error("Consumer error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
