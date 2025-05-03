package xyz.delartigue.gotdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GotDemoApplication

fun main(args: Array<String>) {
    runApplication<GotDemoApplication>(*args)
}
