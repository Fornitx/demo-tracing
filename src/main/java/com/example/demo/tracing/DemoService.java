package com.example.demo.tracing;

import brave.baggage.BaggageField;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
class DemoService {
    @NewSpan("SPAN_2")
    public String getDemo() {
        log.info("getAllValues - {}", BaggageField.getAllValues().size());
        var value = BaggageUtils.get("abc");
        log.info("DemoService.getDemo() - '{}'", value);
        return "Response: " + LocalDateTime.now();
    }
}
