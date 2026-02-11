package com.example.demo.tracing;

import brave.Tracing;
import brave.TracingCustomizer;
import brave.handler.SpanHandler;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation;
import brave.sampler.Sampler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.IncompatibleConfigurationException;
import org.springframework.boot.micrometer.tracing.autoconfigure.TracingProperties;
import org.springframework.boot.micrometer.tracing.brave.autoconfigure.BraveTracingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * copy/paste from BraveAutoConfiguration
 * @see org.springframework.boot.micrometer.tracing.brave.autoconfigure.BraveAutoConfiguration
 */
@Configuration
@RequiredArgsConstructor
public class TracingConfig {
    private static final String DEFAULT_APPLICATION_NAME = "application";

    private final TracingProperties tracingProperties;
    private final BraveTracingProperties braveTracingProperties;

    @Bean
    public Tracing braveTracing(
        Environment environment,
        List<SpanHandler> spanHandlers,
        List<TracingCustomizer> tracingCustomizers,
        CurrentTraceContext currentTraceContext,
        Propagation.Factory propagationFactory,
        Sampler sampler
    ) {
        if (this.braveTracingProperties.isSpanJoiningSupported()) {
            if (this.tracingProperties.getPropagation().getType() != null
                && this.tracingProperties.getPropagation().getType().contains(TracingProperties.Propagation.PropagationType.W3C)) {
                throw new IncompatibleConfigurationException("management.tracing.propagation.type",
                    "management.brave.tracing.span-joining-supported");
            }
            if (this.tracingProperties.getPropagation().getType() == null
                && this.tracingProperties.getPropagation().getProduce().contains(TracingProperties.Propagation.PropagationType.W3C)) {
                throw new IncompatibleConfigurationException("management.tracing.propagation.produce",
                    "management.brave.tracing.span-joining-supported");
            }
            if (this.tracingProperties.getPropagation().getType() == null
                && this.tracingProperties.getPropagation().getConsume().contains(TracingProperties.Propagation.PropagationType.W3C)) {
                throw new IncompatibleConfigurationException("management.tracing.propagation.consume",
                    "management.brave.tracing.span-joining-supported");
            }
        }
        var applicationName = environment.getProperty("spring.application.name", DEFAULT_APPLICATION_NAME);
        var builder = Tracing.newBuilder()
            .currentTraceContext(currentTraceContext)
            .traceId128Bit(false) // !!! the only line changed
            .supportsJoin(this.braveTracingProperties.isSpanJoiningSupported())
            .propagationFactory(propagationFactory)
            .sampler(sampler)
            .localServiceName(applicationName);
        spanHandlers.forEach(builder::addSpanHandler);
        for (var tracingCustomizer : tracingCustomizers) {
            tracingCustomizer.customize(builder);
        }
        return builder.build();
    }
}
