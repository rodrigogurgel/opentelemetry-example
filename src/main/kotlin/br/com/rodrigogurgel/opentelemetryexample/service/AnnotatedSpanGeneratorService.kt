package br.com.rodrigogurgel.opentelemetryexample.service

import io.opentelemetry.api.trace.Span
import io.opentelemetry.extension.kotlin.asContextElement
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("annotatedSpanGeneratorService")
class AnnotatedSpanGeneratorService : SpanGenerator {

    private val logger = LoggerFactory.getLogger(AnnotatedSpanGeneratorService::class.java)

    companion object {
        private val fireAndForgetDispatcher =
            Executors.newFixedThreadPool(10) { r -> Thread(r, "fire-forget-dispatcher") }
                .asCoroutineDispatcher()

        private val fireAndForgetScope = CoroutineScope(fireAndForgetDispatcher + CoroutineName("fire-forget-scope"))
    }

    fun justAString() = "Just a string"

    @WithSpan("CurrentMillis custom name")
    private fun currentMillis() = logger.info("Just a currentMillis: ${System.currentTimeMillis()}")

    @WithSpan
    override fun generateSpan() = parentSpan()

    @WithSpan
    private fun parentSpan(): String = justAString()

    @WithSpan
    override suspend fun generateSuspendSpan(): String = suspendParentSpan()

    @WithSpan
    private suspend fun suspendParentSpan(): String {
        fireAndForgetScope.launch(Span.current().asContextElement()) {
            delay(3000)
            currentMillis()
        }
        return justAString()
    }

    @WithSpan
    override fun generateReactiveSpan() = reactiveParentSpan()

    @WithSpan
    fun reactiveParentSpan(): Mono<String> = Mono.just(fireAndForgetScope.launch {
        delay(3000)
        currentMillis()
    }).then(Mono.just(justAString()))
}
