package com.example.demo.tracing;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.example.demo.tracing.DemoController.DEMO;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureTracing
@Slf4j
class DemoControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void test1() {
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
    }

    @Test
    void test2() {
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
    }
}
