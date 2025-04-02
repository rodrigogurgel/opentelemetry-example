package br.com.rodrigogurgel.opentelemetryexample.controller

import br.com.rodrigogurgel.opentelemetryexample.service.SpanGenerator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/annotated/trace")
class AnnotatedTraceController(
    @Qualifier("annotatedSpanGeneratorService")
    private val annotatedSpanGeneratorService: SpanGenerator
) {
    @GetMapping("/no-span")
    fun noSpan(): String = "Just a no-span"

    @GetMapping("/span")
    fun span(): String = annotatedSpanGeneratorService.generateSpan()

    @GetMapping("/suspend-span")
    suspend fun suspendSpan(): String = annotatedSpanGeneratorService.generateSuspendSpan()

    @GetMapping("/reactive-span")
    fun reactiveSpan(): Mono<String> = annotatedSpanGeneratorService.generateReactiveSpan()
}