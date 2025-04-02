package br.com.rodrigogurgel.opentelemetryexample.extensions.reactive

import br.com.rodrigogurgel.opentelemetryexample.extensions.defaultSpanName
import br.com.rodrigogurgel.opentelemetryexample.extensions.spanBuilder
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

inline fun <T> monoSpan(
    name: String = defaultSpanName(),
    apply: (SpanBuilder) -> SpanBuilder = { it },
    crossinline block: (span: Span?) -> Mono<T>
): Mono<T> {
    val span = spanBuilder("inline-reactive-span", name)
        .apply { apply(this) }
        .startSpan()

    val scope = span.makeCurrent()
    return runCatching {
        block(span)
    }.onFailure {
        span.recordException(it)
    }.also {
        span.end()
        scope.close()
    }.getOrThrow()
}

inline fun <T> fluxSpan(
    name: String = defaultSpanName(),
    apply: (SpanBuilder) -> SpanBuilder = { it },
    crossinline block: (span: Span?) -> Flux<T>
): Flux<T> {
    val span = spanBuilder("inline-reactive-span", name)
        .apply { apply(this) }
        .startSpan()

    val scope = span.makeCurrent()

    return runCatching {
        block(span)
    }.onFailure {
        span.recordException(it)
    }.also {
        span.end()
        scope.close()
    }.getOrThrow()
}
