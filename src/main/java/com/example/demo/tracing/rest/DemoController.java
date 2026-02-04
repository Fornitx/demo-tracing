package com.example.demo.tracing.rest;

import com.example.demo.tracing.utils.BaggageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    public static final String DEMO = "/demo";

    private final DemoService service;

    private final ThreadLocal<Integer> tl = ThreadLocal.withInitial(() -> 111);

    @PostMapping(DEMO)
    public Mono<String> postDemoReactive(@RequestBody Mono<String> body) {
        log.info("BaggageUtils.set");
        BaggageUtils.set("abc", "xyz");

        tl.set(222);

        log.info("Request: POST {}: {}", DEMO, body);

        return body
            .delayElement(Duration.ofMillis(1))
            .doOnNext(_ -> {
                log.info("ThreadLocal = {}", tl.get());
            })
            .flatMap(service::postDemoReactive);
    }
}
