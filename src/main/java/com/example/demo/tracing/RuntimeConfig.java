package com.example.demo.tracing;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static com.example.demo.tracing.RuntimeConfig.BATCH_PROFILE;
import static net.logstash.logback.marker.Markers.appendEntries;

@Profile(BATCH_PROFILE)
@Configuration
@RequiredArgsConstructor
public class RuntimeConfig {
    public static final String BATCH_PROFILE = "batch";

    private final Tracer tracer;

    @Bean
    public RuntimeComponent runtimeComponent() {
        return new RuntimeComponent(tracer);
    }

    @Bean
    public ReactiveRuntimeComponent reactiveRuntimeComponent() {
        return new ReactiveRuntimeComponent(tracer);
    }

    @RequiredArgsConstructor
    @Slf4j
    public static class RuntimeComponent {
        private final Tracer tracer;

        @EventListener(ApplicationStartedEvent.class)
        public void start() {
            var span = tracer.nextSpan().name("span1");
            try (var _ = tracer.withSpan(span.start())) {
                tracer.getBaggage("abc").makeCurrent("xyz");
                log.info("Started");
            }
        }
    }

    @RequiredArgsConstructor
    @Slf4j
    public static class ReactiveRuntimeComponent {
        private final Tracer tracer;

        @EventListener(ApplicationStartedEvent.class)
        public void start() throws InterruptedException {
            var latch = new CountDownLatch(1);

            var span = tracer.nextSpan().name("span2").start();
            Mono.fromCallable(() -> 123)
                .delayElement(Duration.ofMillis(100))
                .doOnNext(_ -> {
                    try (var _ = tracer.withSpan(span)) {
                        tracer.getBaggage("abc").makeCurrent("xyz");
                    }
                })
                .delayElement(Duration.ofMillis(100))
                .doOnNext(_ -> {
                    try (var _ = tracer.withSpan(span)) {
                        log.info(appendEntries(tracer.getAllBaggage()), "Started 1");
                        log.info("Started 2", StructuredArguments.entries(tracer.getAllBaggage()));
                        log.info("Started 3: abc = {}", tracer.getBaggage("abc").get());
                    }
                })
                .doFinally(_ -> {
                    span.end();
                    latch.countDown();
                })
                .subscribe();

            latch.await();
        }
    }
}
