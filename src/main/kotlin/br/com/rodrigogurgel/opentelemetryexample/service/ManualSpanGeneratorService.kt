package br.com.rodrigogurgel.opentelemetryexample.service

import br.com.rodrigogurgel.opentelemetryexample.extensions.coroutines.suspendSpan
import br.com.rodrigogurgel.opentelemetryexample.extensions.reactive.monoSpan
import br.com.rodrigogurgel.opentelemetryexample.extensions.span
import io.opentelemetry.api.trace.Span
import io.opentelemetry.extension.kotlin.asContextElement
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("manualSpanGeneratorService")
class ManualSpanGeneratorService : SpanGenerator {
    private val logger = LoggerFactory.getLogger(ManualSpanGeneratorService::class.java)

    companion object {
        private val fireAndForgetDispatcher =
            Executors.newFixedThreadPool(10) { r -> Thread(r, "fire-forget-dispatcher") }
                .asCoroutineDispatcher()

        private val fireAndForgetScope = CoroutineScope(fireAndForgetDispatcher + CoroutineName("fire-forget-scope"))
    }

    fun justAString() = "Just a string"

    private fun currentMillis() = span("CurrentMillis custom name") {
        logger.info("Just a currentMillis: ${System.currentTimeMillis()}")
    }

    override fun generateSpan() = span { parentSpan() }

    private fun parentSpan(): String = span {
        justAString()
    }

    override suspend fun generateSuspendSpan(): String = suspendSpan {
        suspendParentSpan()
    }

    private suspend fun suspendParentSpan(): String = suspendSpan {
        fireAndForgetScope.launch(Span.current().asContextElement() + CoroutineName("suspend-parent-span")) {
            delay(3000)
            currentMillis()
        }
        justAString()
    }

    override fun generateReactiveSpan() = monoSpan {
        reactiveParentSpan()
    }

    fun reactiveParentSpan(): Mono<String> = monoSpan {
        mono(Span.current().asContextElement()) {
            fireAndForgetScope.launch {
                delay(3000)
                currentMillis()
            }
        }.then(Mono.just(justAString()))
    }
}