package com.example.demo.tracing.kafka.examples;

import com.example.demo.tracing.DemoTracingApplication;
import com.example.demo.tracing.kafka.tc.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestDemoTracingApplication {
    public static void main(String[] args) {
        SpringApplication.from(DemoTracingApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
