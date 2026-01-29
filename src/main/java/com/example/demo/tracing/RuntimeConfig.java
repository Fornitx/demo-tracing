package com.example.demo.tracing;

import com.example.demo.tracing.utils.Profiles;
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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static net.logstash.logback.marker.Markers.appendEntries;

@Profile(Profiles.BATCH)
@Configuration
@RequiredArgsConstructor
public class RuntimeConfig {
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
            for (var i = 0; i < 5; i++) {
                var span = tracer.nextSpan().name("span1");
                try (var _ = tracer.withSpan(span.start())) {
                    tracer.getBaggage("abc").makeCurrent("xyz " + i);
                    log.info("Started {}", i);
                }
            }
        }
    }

    @RequiredArgsConstructor
    @Slf4j
    public static class ReactiveRuntimeComponent {
        private final Tracer tracer;
        private final Scheduler scheduler = Schedulers.newSingle("abc");

        @EventListener(ApplicationStartedEvent.class)
        public void start() throws InterruptedException {
            for (var i = 0; i < 5; i++) {
                var finalI = i;
                var latch = new CountDownLatch(1);

                var span = tracer.nextSpan().name("span2").start();
                Mono.fromCallable(() -> 123)
                    .publishOn(scheduler)
                    .delayElement(Duration.ofMillis(100))
                    .doOnNext(_ -> {
                        try (var _ = tracer.withSpan(span)) {
                            tracer.getBaggage("abc").makeCurrent("xyz");
                        }
                    })
                    .delayElement(Duration.ofMillis(100))
                    .doOnNext(_ -> {
                        try (var _ = tracer.withSpan(span)) {
                            log.info(appendEntries(tracer.getAllBaggage()), "Started {}-1", finalI);
                            log.info("Started {}-2", finalI, StructuredArguments.entries(tracer.getAllBaggage()));
                            log.info("Started {}-3: abc = {}", finalI, tracer.getBaggage("abc").get());
                        }
                    })
                    .doFinally(_ -> {
                        span.end();
                        latch.countDown();
                    })
                    .subscribeOn(scheduler)
                    .subscribe();

                latch.await();
            }
        }
    }
}
