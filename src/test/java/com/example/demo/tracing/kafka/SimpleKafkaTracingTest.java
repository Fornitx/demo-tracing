package com.example.demo.tracing.kafka;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.EnableWireMock;

import java.util.concurrent.TimeUnit;

import static com.example.demo.tracing.utils.Constants.TOPIC_1;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@AutoConfigureTracing
@EnableWireMock
class SimpleKafkaTracingTest extends AbstractKafkaTracingTest {
    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    @SneakyThrows
    @Test
    void test() {
        stubFor(post("/test").willReturn(ok("pong")));

        produce(TOPIC_1, "ABC");
        produce(TOPIC_1, "XYZ");

        TimeUnit.SECONDS.sleep(10);
    }
}
