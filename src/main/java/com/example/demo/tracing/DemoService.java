package com.example.demo.tracing;

import brave.baggage.BaggageField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
class DemoService {
    public String postDemo(String body) {
        log.info("getAllValues - {}", BaggageField.getAllValues().size());
        var value = Objects.requireNonNull(BaggageUtils.get("abc"));
        log.info("DemoService.postDemo({}) - '{}'", body, value);
        return "Response: " + LocalDateTime.now();
    }

    public Mono<String> postDemoReactive(Mono<String> bodyMono) {
        return bodyMono.flatMap(body -> {
            return Mono.fromCallable(() -> {
                log.info("getAllValues - {}", BaggageField.getAllValues().size());
                var value = Objects.requireNonNull(BaggageUtils.get("abc"));
                log.info("DemoService.postDemoReactive({}) - '{}'", body, value);
                return "Response: " + LocalDateTime.now();
            });
        });
    }
}
