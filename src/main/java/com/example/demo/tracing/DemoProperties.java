package com.example.demo.tracing;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("demo")
public record DemoProperties(
    @NonNull RuntimeType runtime
) {
    public enum RuntimeType {
        none, basic, reactive
    }
}
