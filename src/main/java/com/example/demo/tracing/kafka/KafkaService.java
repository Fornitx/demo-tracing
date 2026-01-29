package com.example.demo.tracing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaSleuthReceiver<String, String> receiver;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        receiver.receive()
            .doOnNext(receiverRecord -> receiverRecord.receiverOffset().acknowledge())
            .flatMap(this::processRecord)
            .subscribe();
    }

    private Mono<?> processRecord(ReceiverRecord<String, String> receiverRecord) {
        return Mono.just(receiverRecord)
            .doOnNext(record -> {
                log.info("ReceiverRecord {}", record.receiverOffset());
            });
    }
}
