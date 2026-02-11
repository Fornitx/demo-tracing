package com.example.demo.tracing.rest;

import com.example.demo.tracing.bh.BlackHole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.example.demo.tracing.rest.DemoController.DEMO;
import static com.example.demo.tracing.utils.Constants.X_TRACE_ID;
import static com.example.demo.tracing.utils.TracingUtils.newTraceIdHeaderValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureTracing
@Slf4j
class DemoControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BlackHole bh;

    @RepeatedTest(2)
    void testReactive() {
        var rawResponseBody = webTestClient.post()
            .uri(DEMO)
            .bodyValue("123")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

        log.info("rawResponseBody = {}", rawResponseBody);

        verify(bh).traceIdAndSpanId(anyString(), anyString());
    }

    @RepeatedTest(2)
    void testReactiveWithHeader() {
        var traceId = newTraceIdHeaderValue();
        log.info("Starting with traceId = {}", traceId);

        var rawResponseBody = webTestClient.post()
            .uri(DEMO)
            .header(X_TRACE_ID, traceId)
            .bodyValue("123")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

        log.info("rawResponseBody = {}", rawResponseBody);

        verify(bh).traceIdAndSpanId(anyString(), anyString());
    }
}
