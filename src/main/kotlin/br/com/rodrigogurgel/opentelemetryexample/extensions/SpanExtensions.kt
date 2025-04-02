package br.com.rodrigogurgel.opentelemetryexample.extensions

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context

fun spanBuilder(
    instrumentationScopeName: String,
    spanName: String = defaultSpanName(),
    apply: (SpanBuilder) -> SpanBuilder = { it },
): SpanBuilder {
    val tracer: Tracer = GlobalOpenTelemetry.getTracer("${object {}.javaClass.packageName} $instrumentationScopeName")
    return tracer.spanBuilder(spanName)
        .setParent(Context.current())
        .apply { apply(this) }
}

inline fun <T> span(
    name: String = defaultSpanName(),
    apply: (SpanBuilder) -> SpanBuilder = { it },
    crossinline block: (span: Span?) -> T
): T {
    val span: Span = spanBuilder("inline-span", name)
        .apply { apply(this) }
        .startSpan()

    span.makeCurrent().use {
        return runCatching {
            block(span)
        }.onFailure {
            span.recordException(it)
        }.also {
            span.end()
        }.getOrThrow()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun defaultSpanName(): String {
    val callingStackFrame = Thread.currentThread().stackTrace[1]

    val simpleClassName = Class.forName(callingStackFrame.className).simpleName
    val methodName = callingStackFrame.methodName

    return "$simpleClassName.$methodName"
}
