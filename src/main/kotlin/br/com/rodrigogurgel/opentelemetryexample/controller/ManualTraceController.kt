package br.com.rodrigogurgel.opentelemetryexample.controller

import br.com.rodrigogurgel.opentelemetryexample.service.ManualSpanGeneratorService
import br.com.rodrigogurgel.opentelemetryexample.service.SpanGenerator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/manual/trace")
class ManualTraceController(
    @Qualifier("manualSpanGeneratorService")
    private val manualSpanGeneratorService: ManualSpanGeneratorService
) {
    @GetMapping("/no-span")
    fun noSpan(): String = "Just a no-span"

    @GetMapping("/span")
    fun span(): String = manualSpanGeneratorService.generateSpan()

    @GetMapping("/suspend-span")
    suspend fun suspendSpan(): String = manualSpanGeneratorService.generateSuspendSpan()

    @GetMapping("/reactive-span")
    fun reactiveSpan(): Mono<String> = manualSpanGeneratorService.generateReactiveSpan()
}