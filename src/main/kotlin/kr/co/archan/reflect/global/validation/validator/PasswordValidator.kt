package kr.co.archan.reflect.global.validation.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kr.co.archan.reflect.global.util.Validator
import kr.co.archan.reflect.global.validation.annotation.ValidPassword

class PasswordValidator : ConstraintValidator<ValidPassword, String> {
    
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return value?.let { Validator.isPasswordValid(it) } ?: false
    }
}
