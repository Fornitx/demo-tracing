package com.example.demo.tracing.kafka;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.TracingObservationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.observability.micrometer.Micrometer;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaSleuthReceiver<String, String> receiver;
    private final Tracer tracer;
    private final brave.Tracer braveTracer;
    private final ObservationRegistry observationRegistry;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        receiver.receive()
            .doOnNext(receiverRecord -> receiverRecord.obj().receiverOffset().acknowledge())
            .flatMap(spanned ->
                    processRecord(spanned)
                        .tap(Micrometer.observation(
                            observationRegistry,
                            observationRegistry -> Observation.createNotStarted(
                                "reactive_kafka",
                                () -> {
                                    var tracingContext = new TracingObservationHandler.TracingContext();
                                    tracingContext.setSpan(spanned.span());
                                    return new Observation.Context().put(
                                        TracingObservationHandler.TracingContext.class,
                                        tracingContext
                                    );
                                },
                                observationRegistry
                            )
                        ))
            )
            .subscribe();
    }

    //    @NewSpan("SCOPE_2")
    public Mono<?> processRecord(Spanned<ReceiverRecord<String, String>> spannedOrig) {
//        try (var _ = tracer.withSpan(spannedOrig.span())) {
        return Mono.just(spannedOrig)
            .doOnNext(spanned -> {
                log.info("tracer.currentSpan() = {}", tracer.currentSpan());
                log.info("braveTracer.currentSpan() = {}", braveTracer.currentSpan());
                log.info("ReceiverRecord {}", spanned.obj().receiverOffset());
            })
//                .contextCapture()
//            .contextWrite(context -> {
//                ContextSnapshot.setThreadLocalsFrom(context, ObservationThreadLocalAccessor.KEY);
//                return context;
//            })
//            .contextWrite(Context.of(TraceContext.class, spannedOrig.span().context()))
//            .contextWrite(Context.of(Span.class, spannedOrig.span()))
//            .doFinally(__ -> {
//                tracer.currentSpan().end();
//                braveTracer.currentSpan().finish();
//            })
            ;
//        }
    }
}
