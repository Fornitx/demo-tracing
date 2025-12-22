package com.example.demo.tracing;

import brave.baggage.BaggageField;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
class DemoService {
    private final Tracer tracer;

    public String postDemo(String body) {
        var span = tracer.currentSpan().context();
        log.info("spanId = '{}', traceId = '{}'", span.spanId(), span.traceId());
        log.info("getAllValues - {}", BaggageField.getAllValues().size());
        var value = Objects.requireNonNull(BaggageUtils.get("abc"));
        log.info("DemoService.postDemo({}) - '{}'", body, value);
        return "Response: " + LocalDateTime.now();
    }

    public Mono<String> postDemoReactive(Mono<String> bodyMono) {
        return bodyMono.delayElement(Duration.ofMillis(200)).flatMap(body -> {
            return Mono.fromCallable(() -> {
                var span = tracer.currentSpan().context();
                log.info("spanId = '{}', traceId = '{}'", span.spanId(), span.traceId());
                log.info("getAllValues - {}", BaggageField.getAllValues().size());
                var value = Objects.requireNonNull(BaggageUtils.get("abc"));
                log.info("DemoService.postDemoReactive({}) - '{}'", body, value);
                return "Response: " + LocalDateTime.now();
            });
        });
    }
}
