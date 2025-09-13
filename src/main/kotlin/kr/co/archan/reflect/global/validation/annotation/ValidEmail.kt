package kr.co.archan.reflect.global.validation.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kr.co.archan.reflect.global.validation.validator.EmailValidator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EmailValidator::class])
annotation class ValidEmail(
    val message: String = "email",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)