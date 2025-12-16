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

    private final Tracer tracer;
    private final DemoService service;

    @GetMapping(DEMO)
    @NewSpan("SPAN_1")
    public String getDemo() {
        tracer.currentSpan().tag("abg1", "xyz1");

        BaggageUtils.set("abc", "xyz");

        log.info("Request: GET {}", DEMO);
        var response = service.getDemo();

        tracer.currentSpan().end();

        return response;
    }
}
