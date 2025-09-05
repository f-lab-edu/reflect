package kr.co.archan.reflect

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ReflectBeApplication

fun main(args: Array<String>) {
    runApplication<ReflectBeApplication>(*args)
}
