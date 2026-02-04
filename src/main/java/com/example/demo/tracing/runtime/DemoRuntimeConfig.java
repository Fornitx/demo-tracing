package com.example.demo.tracing.runtime;

import com.example.demo.tracing.DemoProperties;
import com.example.demo.tracing.bh.BlackHole;
import com.example.demo.tracing.utils.Profiles;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile(Profiles.BATCH)
@Configuration
@RequiredArgsConstructor
public class DemoRuntimeConfig {
    private final ObservationRegistry observationRegistry;
    private final Tracer tracer;

    private final DemoProperties properties;
    private final BlackHole bh;

    @Bean
    public RuntimeComponent runtimeComponent() {
        return switch (properties.runtime()) {
            // @formatter:off
            case none -> () -> {};
            // @formatter:on
            case basic -> new RuntimeComponentImpl(bh, tracer);
            case reactive -> new ReactiveRuntimeComponentImpl(bh, tracer, observationRegistry);
        };
    }

}
