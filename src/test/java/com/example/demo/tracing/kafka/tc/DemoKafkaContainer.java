package com.example.demo.tracing.kafka.tc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static com.example.demo.tracing.kafka.tc.TestcontainersConfiguration.DOCKER_IMAGE_NAME;

@Slf4j
public class DemoKafkaContainer extends KafkaContainer {
    public static final String TOPIC_1 = "topic1";
    private static final KafkaContainer INSTANCE = new DemoKafkaContainer(DOCKER_IMAGE_NAME).withReuse(true);

    public static KafkaContainer instance() {
        return INSTANCE;
    }

    private DemoKafkaContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    @Override
    public void start() {
        super.start();

        log.info("KafkaContainer started: {}", getBootstrapServers());

        System.setProperty("TC_KAFKA", getBootstrapServers());

        createTopics();
    }

    @SneakyThrows
    private void createTopics() {
        var topics = List.of(
            TopicBuilder.name(TOPIC_1).partitions(1).build()
        );

        try (var adminClient = AdminClient.create(
            Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers())
        )) {
            var actualTopics = adminClient.listTopics().names().get();
            if (!actualTopics.containsAll(topics.stream().map(NewTopic::name).toList())) {
                adminClient.createTopics(topics).all().get();
            }
        }
    }
}
