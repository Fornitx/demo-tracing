package com.example.demo.tracing.runtime;

import com.example.demo.tracing.bh.BlackHole;
import com.example.demo.tracing.kafka.Spanned;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import reactor.core.observability.micrometer.Micrometer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class ReactiveRuntimeComponentImpl implements RuntimeComponent {
    private final BlackHole bh;
    private final Tracer tracer;
    private final ObservationRegistry observationRegistry;

    @EventListener(ApplicationStartedEvent.class)
    @Override
    public void start() {
//        startClean();
//        startDirty();
        startDirty2();
    }

    private void startDirty2() {
        Flux.range(0, 5)
            .delayElements(Duration.ofMillis(10))
            .flatMap(idx ->
                Mono.fromCallable(() -> {
                        log.info("map(idx={})", idx);
                        var span = tracer.currentSpan();
                        log.info("span created: {}", idx);
                        tracer.getBaggage(span.context(), "abc").makeCurrent(span.context(), "xyz " + idx);
                        log.info("span started: {}", idx);
                        return idx;
                    })
                    .delayElement(Duration.ofMillis(10))
                    .doOnNext(idx2 -> {
                        log.info("Started {}", idx2);
                        var context = tracer.currentSpan().context();
                        bh.traceIdAndSpanId(context.traceId(), context.spanId());
                    })
                    .tap(Micrometer.observation(observationRegistry)))
            .subscribe();
    }

    private void startDirty() {
        Flux.range(0, 5)
            .doOnNext(idx -> {
//                Observation.start("reactive_span", observationRegistry).openScope();
//                tracer.nextSpan();

//                log.info("map(idx={})", idx);
//                var span = tracer.nextSpan().name("reactive_span");
//                log.info("span created: {}", idx);
//                tracer.getBaggage(span.context(), "abc").makeCurrent(span.context(), "xyz " + idx);
//                tracer.withSpan(span);
//                log.info("span started: {}", idx);
            })
            .delayElements(Duration.ofMillis(10))
            .doOnNext(idx -> {
                log.info("Started {}", idx);
                var context = tracer.currentSpan().context();
                bh.traceIdAndSpanId(context.traceId(), context.spanId());
            })
            .tap(Micrometer.observation(observationRegistry))
            .subscribe();
    }

    private void startClean() {
        Flux.range(0, 5)
            .flatMap(idx -> {
                log.info("map(idx={})", idx);
                var span = tracer.nextSpan().name("reactive_span");
                log.info("span created: {}", idx);
                tracer.getBaggage(span.context(), "abc").makeCurrent(span.context(), "xyz " + idx);
                return Mono.just(new Spanned<>(idx, span));
            })
            .delayElements(Duration.ofMillis(10))
            .doOnNext(spanned -> {
                var idx = spanned.obj();
                try (var _ = tracer.withSpan(spanned.span())) {
                    log.info("Started {}", idx);
                    var context = tracer.currentSpan().context();
                    bh.traceIdAndSpanId(context.traceId(), context.spanId());
                }
            })
            .delayElements(Duration.ofMillis(10))
            .doOnNext(spanned -> {
                var idx = spanned.obj();
                log.info("Ended {}", idx);
            })
            .subscribe();
    }
}
