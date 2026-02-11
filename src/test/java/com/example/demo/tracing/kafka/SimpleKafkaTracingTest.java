package com.example.demo.tracing.kafka;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.EnableWireMock;

import java.util.concurrent.TimeUnit;

import static com.example.demo.tracing.utils.Constants.TOPIC_1;
import static com.example.demo.tracing.utils.Constants.X_TRACE_ID;
import static com.example.demo.tracing.utils.TracingUtils.newTraceIdHeaderValue;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@AutoConfigureTracing
@EnableWireMock
@Slf4j
class SimpleKafkaTracingTest extends AbstractKafkaTracingTest {
    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    @SneakyThrows
    @Test
    void test() {
        stubFor(post("/test").willReturn(ok("pong")));

        var traceId1 = newTraceIdHeaderValue();
        var traceId2 = newTraceIdHeaderValue();

        produce(new ProducerRecord<>(
            TOPIC_1,
            null,
            null,
            null,
            "ABC",
            new RecordHeaders().add(new RecordHeader(X_TRACE_ID, traceId1.getBytes()))
        ));
        produce(new ProducerRecord<>(
            TOPIC_1,
            null,
            null,
            null,
            "XYZ",
            new RecordHeaders().add(new RecordHeader(X_TRACE_ID, traceId2.getBytes()))
        ));

        log.info("Sent traceId1 = {}", traceId1);
        log.info("Sent traceId2 = {}", traceId2);

        TimeUnit.SECONDS.sleep(10);
    }
}
