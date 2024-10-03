package dev.kukukodes.kdap.actionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ActionServiceApplication

fun main(args: Array<String>) {
    runApplication<ActionServiceApplication>(*args)
}
