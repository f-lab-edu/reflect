package kr.co.archan.reflect.global.validation.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kr.co.archan.reflect.global.validation.validator.PasswordValidator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordValidator::class])
annotation class ValidPassword(
    val message: String = "password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
