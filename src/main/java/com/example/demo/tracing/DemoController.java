package com.example.demo.tracing;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    public static final String DEMO = "/demo";

    private final DemoService service;
    private final Tracer tracer;

    @GetMapping(DEMO)
    @NewSpan("SPAN_1")
    public String getDemo() {
        try (var scope = tracer.createBaggageInScope("baggage", "xyz")) {
            log.info("Request: GET {}", DEMO);
            return service.getDemo(scope);
        }
    }
}
