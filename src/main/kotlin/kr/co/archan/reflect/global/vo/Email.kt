package kr.co.archan.reflect.global.vo

import kr.co.archan.reflect.global.util.Validator
import kr.co.archan.reflect.global.util.extension.requireAndThrowProductException
import kr.co.archan.reflect.member.exception.InvalidEmailException

@JvmInline
value class Email private constructor(private val value: String) {
    companion object {
        fun of(raw: String): Email {
            val v = raw.trim()
            requireAndThrowProductException(Validator.isEmailValid(v))  { InvalidEmailException() }
            return Email(v)
        }
    }

    fun asString() : String = value

    fun domainPart(): String = value.substringAfter("@")
}