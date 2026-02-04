package com.example.demo.tracing.bh;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlackHoleImpl implements BlackHole {
    @Override
    public void traceIdAndSpanId(String traceId, String spanId) {
        log.info("traceIdAndSpanId({}, {})", traceId, spanId);
    }
}
