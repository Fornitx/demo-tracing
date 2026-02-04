package com.example.demo.tracing.kafka;

import com.example.demo.tracing.utils.Profiles;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

import static com.example.demo.tracing.utils.Constants.TOPIC_1;

@Profile(Profiles.KAFKA)
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;
//    private final ObservationRegistry observationRegistry;

    @Bean
    public KafkaReceiver<String, String> receiver() {
        var consumerProperties = kafkaProperties.buildConsumerProperties();
        var receiverOptions = ReceiverOptions.<String, String>create(consumerProperties)
//            .withObservation(observationRegistry, new KafkaReceiverObservation.DefaultKafkaReceiverObservationConvention())
            .subscription(List.of(TOPIC_1));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaService kafkaService(
        KafkaReceiver<String, String> receiver,
        Tracer tracer,
        brave.Tracer braveTracer,
        ObservationRegistry observationRegistry
    ) {
        return new KafkaService(
            new KafkaSleuthReceiver<>(receiver, tracer, braveTracer),
            tracer,
            braveTracer,
            observationRegistry
        );
    }
}
