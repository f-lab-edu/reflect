package kr.co.archan.reflect.global.util

object Validator {
    fun isEmailValid(email: String): Boolean =
        email.isNotBlank() && email.length <= 255 && '@' in email

    fun isNameValid(name: String): Boolean =
        name.isNotBlank() && name.trim().length in 1..20
}