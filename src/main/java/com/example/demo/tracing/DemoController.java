package com.example.demo.tracing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    public static final String DEMO = "/demo";
    public static final String DEMO_REACTIVE = "/demo_reactive";

    private final DemoService service;

    @PostMapping(DEMO)
    public String postDemo(@RequestBody String body) {
        BaggageUtils.set("abc", "xyz");

        log.info("Request: POST {}: {}", DEMO, body);

        return service.postDemo(body);
    }

    @PostMapping(DEMO_REACTIVE)
    public Mono<String> postDemoReactive(@RequestBody Mono<String> body) {
        BaggageUtils.set("abc", "xyz");

        log.info("Request: POST {}: {}", DEMO_REACTIVE, body);

        return service.postDemoReactive(body);
    }
}
