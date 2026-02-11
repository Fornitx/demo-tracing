package com.example.demo.tracing.rest;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${wiremock.server.baseUrl:localhost:8080}")
    private String wireMockUrl;

    private final ConcurrentHashMap<Serializable, WebClient> clients = new ConcurrentHashMap<>();

    @SneakyThrows
    public Mono<String> call(String value) {
        return clients.computeIfAbsent(0, _ -> webClientBuilder.baseUrl(wireMockUrl).build())
            .post()
            .uri("/test")
            .bodyValue(value)
            .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class));
    }
}
