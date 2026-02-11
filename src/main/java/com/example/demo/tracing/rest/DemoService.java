package com.example.demo.tracing.rest;

import com.example.demo.tracing.bh.BlackHole;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoService {
    private final BlackHole bh;
    private final Tracer tracer;

    public Mono<String> postDemoReactive(String body) {
        return Mono.fromCallable(() -> {
            var context = tracer.currentSpan().context();
            log.info("traceId = '{}', spanId = '{}'", context.traceId(), context.spanId());
            bh.traceIdAndSpanId(context.traceId(), context.spanId());
            log.info("getAllValues - {}", tracer.getAllBaggage().size());
            var value = Objects.requireNonNull(tracer.getBaggage("abc").get());
            log.info("DemoService.postDemoReactive({}) - '{}'", body, value);
            return "Response: " + LocalDateTime.now();
        });
    }
}
