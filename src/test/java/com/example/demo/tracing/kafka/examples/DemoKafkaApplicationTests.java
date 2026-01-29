package com.example.demo.tracing.kafka.examples;

import com.example.demo.tracing.kafka.tc.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DemoKafkaApplicationTests {
    @Test
    void contextLoads() {
    }
}
