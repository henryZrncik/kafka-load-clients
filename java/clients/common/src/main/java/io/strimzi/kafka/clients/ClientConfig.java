package io.strimzi.kafka.clients;

import org.apache.kafka.clients.CommonClientConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ClientConfig {
    private static final Logger log = LoggerFactory.getLogger(ClientConfig.class);

    public static Properties createProperties() {
        Properties props = new Properties();

        String bootstrapServers = System.getenv().getOrDefault("BOOTSTRAP_SERVERS", "localhost:9092");
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        log.info("Bootstrap servers: {}", bootstrapServers);

        // Additional config from environment
        String additionalConfig = System.getenv("ADDITIONAL_CONFIG");
        if (additionalConfig != null && !additionalConfig.isEmpty()) {
            for (String config : additionalConfig.split(",")) {
                String[] parts = config.split("=", 2);
                if (parts.length == 2) {
                    props.put(parts[0].trim(), parts[1].trim());
                    log.info("Additional config: {}={}", parts[0].trim(), parts[1].trim());
                }
            }
        }

        return props;
    }
}
