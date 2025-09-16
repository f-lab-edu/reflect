package kr.co.archan.reflect.global.validation.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kr.co.archan.reflect.global.util.Validator
import kr.co.archan.reflect.global.validation.annotation.ValidEmail

class EmailValidator : ConstraintValidator<ValidEmail, String> {
    
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return value?.let { Validator.isEmailValid(it) } ?: false
    }
}
