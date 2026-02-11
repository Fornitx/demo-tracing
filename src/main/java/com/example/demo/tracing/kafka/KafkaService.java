package com.example.demo.tracing.kafka;

import com.example.demo.tracing.rest.DemoClient;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import static com.example.demo.tracing.utils.TracingUtils.trace;

@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaSleuthReceiver<String, String> receiver;
    private final DemoClient demoClient;
    private final Tracer tracer;
    private final ObservationRegistry observationRegistry;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        receiver.receive()
            .doOnNext(receiverRecord -> receiverRecord.obj().receiverOffset().acknowledge())
            .flatMap(spanned -> processRecord(spanned.obj())
                .tap(trace("reactive_kafka", spanned.span(), observationRegistry))
            )
            .subscribe();
    }

    //    @NewSpan("SCOPE_2")
    public Mono<?> processRecord(ReceiverRecord<String, String> recordArg) {
//        try (var _ = tracer.withSpan(spannedOrig.span())) {
        return Mono.just(recordArg)
            .doOnNext(record -> {
                log.info("tracer.currentSpan() = {}", tracer.currentSpan());
                log.info("tracer.currentSpan().context().traceId() = {}", tracer.currentSpan().context().traceId());
                log.info("ReceiverRecord {}", record.receiverOffset());
            })
            .flatMap(record -> demoClient.call(record.value()))
            .doOnNext(str -> log.info("Finish - {}", str))
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
