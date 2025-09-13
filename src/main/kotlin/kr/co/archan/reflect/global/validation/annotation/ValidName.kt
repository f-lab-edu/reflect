package kr.co.archan.reflect.global.validation.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kr.co.archan.reflect.global.validation.validator.NameValidator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NameValidator::class])
annotation class ValidName(
    val message: String = "name",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
