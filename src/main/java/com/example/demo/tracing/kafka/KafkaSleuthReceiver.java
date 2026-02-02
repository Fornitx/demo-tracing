package com.example.demo.tracing.kafka;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class KafkaSleuthReceiver<K, V> {
    private final KafkaReceiver<K, V> receiver;
    private final Tracer tracer;
    private final brave.Tracer braveTracer;

    public Flux<Spanned<ReceiverRecord<K, V>>> receive() {
        return receiver.receive()
//            .delayElements(Duration.ofMillis(1))
            .map(receiverRecord -> {
                log.info("ReceiverRecord before {}", receiverRecord.receiverOffset());
//                tracer.startScopedSpan("SCOPE_1");
//                braveTracer.startScopedSpan("SCOPE_1");
//                braveTracer.withSpanInScope(braveTracer.nextSpan().name("scope_1"));

                var span = tracer.nextSpan().name("SCOPE_1");
                try (var _ = tracer.withSpan(span)) {
                    log.info("ReceiverRecord after {}", receiverRecord.receiverOffset());
                }

                return new Spanned<>(receiverRecord, span);
            })
            .delayElements(Duration.ofMillis(1))
            ;
    }
}
