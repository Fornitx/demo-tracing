package com.example.demo.tracing.kafka;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static com.example.demo.tracing.kafka.tc.DemoKafkaContainer.TOPIC_1;

@SpringBootTest
class SimpleKafkaTracingTest extends AbstractKafkaTracingTest {
    @SneakyThrows
    @Test
    void test() {
        produce(TOPIC_1, "ABC");
        produce(TOPIC_1, "XYZ");

        TimeUnit.SECONDS.sleep(10);
    }
}
