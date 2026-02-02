package com.example.demo.tracing.kafka;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.context.Context;

@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaSleuthReceiver<String, String> receiver;
    private final Tracer tracer;
    private final brave.Tracer braveTracer;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        receiver.receive()
            .doOnNext(receiverRecord -> receiverRecord.obj().receiverOffset().acknowledge())
            .flatMap(this::processRecord)
            .subscribe();
    }

//    @NewSpan("SCOPE_2")
    public Mono<?> processRecord(Spanned<ReceiverRecord<String, String>> spannedOrig) {
        try (var _ = tracer.withSpan(spannedOrig.span())) {
            return Mono.just(spannedOrig)
                .doOnNext(spanned -> {
                    log.info("tracer.currentSpan() = {}", tracer.currentSpan());
                    log.info("braveTracer.currentSpan() = {}", braveTracer.currentSpan());
                    log.info("ReceiverRecord {}", spanned.obj().receiverOffset());
                })
                .contextCapture()
//            .contextWrite(context -> {
//                ContextSnapshot.setThreadLocalsFrom(context, ObservationThreadLocalAccessor.KEY);
//                return context;
//            })
            .contextWrite(Context.of(TraceContext.class, spannedOrig.span().context()))
//            .contextWrite(Context.of(Span.class, spannedOrig.span()))
//            .doFinally(__ -> {
//                tracer.currentSpan().end();
//                braveTracer.currentSpan().finish();
//            })
                ;
        }
    }
}
