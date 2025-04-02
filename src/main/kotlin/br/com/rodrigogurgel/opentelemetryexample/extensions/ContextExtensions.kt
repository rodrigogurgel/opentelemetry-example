package br.com.rodrigogurgel.opentelemetryexample.extensions

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.incubator.propagation.ExtendedContextPropagators
import io.opentelemetry.context.Context

inline fun context(block: () -> Map<String, String>): Context {
    return ExtendedContextPropagators.extractTextMapPropagationContext(block(), GlobalOpenTelemetry.getPropagators())
}
