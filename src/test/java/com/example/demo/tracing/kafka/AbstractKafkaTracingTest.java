package com.example.demo.tracing.kafka;

import com.example.demo.tracing.kafka.tc.DemoKafkaContainer;
import com.example.demo.tracing.utils.Profiles;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.KafkaUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.lifecycle.Startables;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ActiveProfiles(Profiles.KAFKA)
@Slf4j
public abstract class AbstractKafkaTracingTest {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);
    private static final KafkaContainer kafkaContainer = DemoKafkaContainer.instance();

    static {
        Startables.deepStart(kafkaContainer).join();
    }

    protected String getBootstrapServers() {
        return kafkaContainer.getBootstrapServers();
    }

    protected final DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(
        Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers(),
            ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000
        ),
        new StringSerializer(),
        new StringSerializer()
    );

    protected final DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers(),
            ConsumerConfig.GROUP_ID_CONFIG, AbstractKafkaTracingTest.class.getSimpleName(),
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        ),
        new StringDeserializer(),
        new StringDeserializer()
    );

    @SneakyThrows
    protected RecordMetadata produce(String topic, String data) {
        try (var producer = producerFactory.createProducer()) {
            var record = new ProducerRecord<String, String>(topic, data);
            var recordMetadata = producer.send(record).get(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            log.debug("Sent {} as {}", KafkaUtils.format(record), recordMetadata);
            return recordMetadata;
        }
    }

    protected ConsumerRecord<String, String> consumeSingle(String topic) {
        try (var consumer = consumerFactory.createConsumer()) {
            consumer.subscribe(List.of(topic));
            return KafkaTestUtils.getSingleRecord(consumer, topic, DEFAULT_TIMEOUT);
        }
    }

    protected ConsumerRecords<String, String> consume(String topic, Integer minRecords) {
        try (var consumer = consumerFactory.createConsumer()) {
            consumer.subscribe(List.of(topic));
            return KafkaTestUtils.getRecords(consumer, DEFAULT_TIMEOUT, minRecords);
        }
    }
}
