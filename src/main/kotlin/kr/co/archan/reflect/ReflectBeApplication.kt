package kr.co.archan.reflect

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@ConfigurationPropertiesScan
@EnableAspectJAutoProxy
@SpringBootApplication
class ReflectBeApplication

fun main(args: Array<String>) {
    runApplication<ReflectBeApplication>(*args)
}
