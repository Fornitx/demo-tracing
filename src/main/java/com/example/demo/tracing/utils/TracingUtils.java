package com.example.demo.tracing.utils;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.TracingObservationHandler;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomUtils;
import reactor.core.observability.SignalListenerFactory;
import reactor.core.observability.micrometer.Micrometer;

import java.util.HexFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class TracingUtils {
    private static final RandomUtils RANDOM = RandomUtils.insecure();

    public static String newTraceIdHeaderValue() {
        var traceId = Stream.generate(() -> RANDOM.randomBytes(8))
            .limit(2)
            .map(bytes -> HexFormat.of().formatHex(bytes))
            .collect(Collectors.joining(":"));
        if (traceId.length() != 33) {
            throw new IllegalStateException("bad traceId = " + traceId);
        }
        return traceId;
    }

    public static Span newSpanFromTraceIdHeaderValue(String traceId, Tracer tracer) {
        if (traceId.length() != 33) {
            throw new IllegalArgumentException("bad traceId = " + traceId);
        }
        var parts = traceId.split(":");
        var traceContext = tracer.traceContextBuilder()
            .traceId(parts[0])
            .spanId(parts[1])
            .build();
        return tracer.spanBuilder().setParent(traceContext).start();
    }

    public static <T> SignalListenerFactory<T, ?> trace(
        String name,
        Span span,
        ObservationRegistry observationRegistry
    ) {
        return Micrometer.observation(
            observationRegistry,
            registry -> Observation.createNotStarted(
                name,
                () -> {
                    var tracingContext = new TracingObservationHandler.TracingContext();
                    tracingContext.setSpan(span);
                    return new Observation.Context().put(
                        TracingObservationHandler.TracingContext.class,
                        tracingContext
                    );
                },
                registry
            )
        );
    }
}
