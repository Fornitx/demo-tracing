package com.example.demo.tracing.kafka.tc;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    public static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("apache/kafka-native:4.1.1");

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DOCKER_IMAGE_NAME);
    }
}

