package com.example.demo.tracing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
public class DemoController {
    public static final String DEMO = "/demo";

    @GetMapping(DEMO)
    public String getDemo() {
        log.info("Request: GET {}", DEMO);
        return "Response: " + LocalDateTime.now();
    }
}
