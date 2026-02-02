package com.example.demo.tracing.kafka;

import io.micrometer.tracing.Span;
import org.jspecify.annotations.NonNull;

public record Spanned<T>(
    @NonNull T obj, @NonNull Span span
) {
}
