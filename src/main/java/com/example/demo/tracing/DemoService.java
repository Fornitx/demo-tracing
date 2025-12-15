package com.example.demo.tracing;

import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
class DemoService {
    private final Tracer tracer;

    @NewSpan("SPAN_2")
    public String getDemo(BaggageInScope scope) {
        var baggage = tracer.getBaggage("baggage");

        log.info("DemoService.getDemo() - {}", baggage.get());
        return "Response: " + LocalDateTime.now();
    }
}
