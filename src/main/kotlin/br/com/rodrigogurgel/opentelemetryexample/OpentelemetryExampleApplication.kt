package br.com.rodrigogurgel.opentelemetryexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OpentelemetryExampleApplication

fun main(args: Array<String>) {
	runApplication<OpentelemetryExampleApplication>(*args)
}
