# Kafka Loader Clients

Multi-language Kafka client implementations for testing and load generation.

Inspired by [Strimzi test-clients](https://github.com/strimzi/test-clients).

## Structure

```
kafka-loader-clients/
├── java/
│   └── clients/
│       ├── common/         # Shared utilities
│       ├── producer/       # Kafka producer client
│       └── consumer/       # Kafka consumer client
├── examples/               # Kubernetes manifests
└── docker-compose.yml      # Local Kafka setup
```


### Build All

```bash
make all
```



## Java Clients

### Producer

Produces messages to a Kafka topic at a configurable rate.

**Environment Variables:**
- `BOOTSTRAP_SERVERS` - Kafka bootstrap servers (default: `localhost:9092`)
- `TOPIC` - Target topic name (default: `test-topic`)
- `MESSAGE_COUNT` - Number of messages to send (default: `1000`)
- `DELAY_MS` - Delay between messages in milliseconds (default: `1000`)

### Consumer

Consumes messages from a Kafka topic.

**Environment Variables:**
- `BOOTSTRAP_SERVERS` - Kafka bootstrap servers (default: `localhost:9092`)
- `TOPIC` - Topic to consume from (default: `test-topic`)
- `GROUP_ID` - Consumer group ID (default: `test-consumer-group`)

