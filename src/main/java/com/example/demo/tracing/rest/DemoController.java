package com.example.demo.tracing.rest;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static com.example.demo.tracing.utils.Constants.X_TRACE_ID;
import static com.example.demo.tracing.utils.TracingUtils.newSpanFromTraceIdHeaderValue;
import static com.example.demo.tracing.utils.TracingUtils.trace;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    public static final String DEMO = "/demo";

    private final ObservationRegistry observationRegistry;
    private final Tracer tracer;

    private final DemoService service;

    private final ThreadLocal<Integer> tl = ThreadLocal.withInitial(() -> 111);

    @PostMapping(DEMO)
    public Mono<String> postDemoReactive(
        @RequestHeader(value = X_TRACE_ID, required = false) String outerTraceId,
        @RequestBody Mono<String> body
    ) {
        var span = Optional.ofNullable(outerTraceId)
            .map(traceId -> newSpanFromTraceIdHeaderValue(traceId, tracer))
            .orElse(tracer.currentSpan());


        return Mono.fromRunnable(() -> {
                log.info("tracer.getBaggage.set");
                tracer.getBaggage("abc").set("xyz");

                tl.set(222);

                log.info("Request: POST {}: {}", DEMO, body);
            })
            .then(body)
            .delayElement(Duration.ofMillis(1))
            .doOnNext(_ -> {
                log.info("ThreadLocal = {}", tl.get());
            })
            .flatMap(service::postDemoReactive)
            .tap(trace("DemoController", span, observationRegistry));
    }
}
