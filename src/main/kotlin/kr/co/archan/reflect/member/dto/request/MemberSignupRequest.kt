package kr.co.archan.reflect.member.dto.request

import kr.co.archan.reflect.global.util.Validator
import kr.co.archan.reflect.global.util.extension.requireAndThrowProductException
import kr.co.archan.reflect.member.exception.InvalidEmailException
import kr.co.archan.reflect.member.exception.InvalidNameException
import kr.co.archan.reflect.member.exception.InvalidPasswordException

data class MemberSignupRequest(
    val email: String,
    val password: String,
    val name: String
) {
    init {
        requireAndThrowProductException(Validator.isNameValid(name)) { InvalidNameException() }
        requireAndThrowProductException(Validator.isEmailValid(email)) { InvalidEmailException() }
        requireAndThrowProductException(Validator.isPasswordValid(password)) { InvalidPasswordException() }
    }
}