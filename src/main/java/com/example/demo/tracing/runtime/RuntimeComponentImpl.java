package com.example.demo.tracing.runtime;

import com.example.demo.tracing.bh.BlackHole;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
@Slf4j
public class RuntimeComponentImpl implements RuntimeComponent {
    private final BlackHole bh;
    private final Tracer tracer;

    @EventListener(ApplicationStartedEvent.class)
    @Override
    public void start() {
        for (var i = 0; i < 5; i++) {
            log.info("Iteration {}", i);
            var span = tracer.nextSpan().name("basic_span");
            log.info("Span created {}", i);
            try (var _ = tracer.withSpan(span)) {
                tracer.getBaggage("abc").makeCurrent("xyz " + i);
                log.info("Started {}", i);
                var context = tracer.currentSpan().context();
                bh.traceIdAndSpanId(context.traceId(), context.spanId());
            }
        }
    }
}
