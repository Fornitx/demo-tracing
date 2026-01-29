package com.example.demo.tracing.kafka;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@RequiredArgsConstructor
@Slf4j
public class KafkaSleuthReceiver<K, V> {
    private final KafkaReceiver<K, V> receiver;
    private final Tracer tracer;

    public Flux<ReceiverRecord<K, V>> receive() {
        return receiver.receive()
            .doOnNext(receiverRecord -> {
                log.info("ReceiverRecord before {}", receiverRecord.receiverOffset());
                tracer.startScopedSpan("SCOPE_1");
                log.info("ReceiverRecord after {}", receiverRecord.receiverOffset());
            });
    }
}
