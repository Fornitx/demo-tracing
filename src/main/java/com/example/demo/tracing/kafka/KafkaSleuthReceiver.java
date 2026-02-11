package com.example.demo.tracing.kafka;

import com.example.demo.tracing.kafka.deprecated.ReactiveKafkaConsumerTemplate;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class KafkaSleuthReceiver<K, V> {
    private final ReactiveKafkaConsumerTemplate<K, V> receiver;
    private final Tracer tracer;

    public Flux<Spanned<ReceiverRecord<K, V>>> receive() {
        return receiver.receive()
//            .delayElements(Duration.ofMillis(1))
            .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(1)))
            .onErrorContinue(((throwable, record) -> {
                log.error(throwable.getMessage(), throwable);
            }))
            .handle((receiverRecord, sink) -> {
                try {
                    log.info("ReceiverRecord before {}", receiverRecord.receiverOffset());
                    var span = tracer.nextSpan().name("SCOPE_1");
                    tracer.getBaggage(span.context(), "abc")
                        .makeCurrent(span.context(), "xyz " + receiverRecord.receiverOffset().toString());
                    try (var _ = tracer.withSpan(span)) {
                        log.info("ReceiverRecord in span {}", receiverRecord.receiverOffset());
                    }
                    log.info("ReceiverRecord after {}", receiverRecord.receiverOffset());

                    sink.next(new Spanned<>(receiverRecord, span));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            })
//            .delayElements(Duration.ofMillis(1))
            ;
    }
}
