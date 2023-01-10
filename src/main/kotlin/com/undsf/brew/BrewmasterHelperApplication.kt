package com.undsf.brew

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class BrewmasterHelperApplication

fun main(args: Array<String>) {
    runApplication<BrewmasterHelperApplication>(*args)
}
