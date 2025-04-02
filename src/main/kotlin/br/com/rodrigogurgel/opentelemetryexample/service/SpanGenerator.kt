package br.com.rodrigogurgel.opentelemetryexample.service

import reactor.core.publisher.Mono

interface SpanGenerator {
    fun generateSpan(): String
    suspend fun generateSuspendSpan(): String
    fun generateReactiveSpan(): Mono<String>
}