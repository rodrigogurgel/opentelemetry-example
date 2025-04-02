package br.com.rodrigogurgel.opentelemetryexample.extensions.coroutines

import br.com.rodrigogurgel.opentelemetryexample.extensions.defaultSpanName
import br.com.rodrigogurgel.opentelemetryexample.extensions.spanBuilder
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
suspend inline fun <T> suspendSpan(
    name: String = defaultSpanName(),
    apply: (SpanBuilder) -> SpanBuilder = { it },
    crossinline block: suspend (span: Span?) -> T
): T {
    val span: Span = spanBuilder("inline-suspend-span", name)
        .apply {
            coroutineContext[CoroutineName]?.let { setAttribute("coroutine.name", it.name) }
        }
        .apply { apply(this) }
        .startSpan()

    return withContext(span.asContextElement()) {
        runCatching {
            block(span)
        }.onFailure {
            span.recordException(it)
        }.also {
            span.end()
        }.getOrThrow()
    }
}
