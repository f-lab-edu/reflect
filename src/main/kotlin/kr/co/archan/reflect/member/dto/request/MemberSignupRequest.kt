package kr.co.archan.reflect.member.dto.request

import kr.co.archan.reflect.global.exception.common.InvalidInputException
import kr.co.archan.reflect.global.util.Validator
import kr.co.archan.reflect.global.util.extension.requireAndThrowProductException
import kr.co.archan.reflect.member.exception.types.MemberInvalidInputField

data class MemberSignupRequest(
    val email: String,
    val password: String,
    val name: String
) {
    init {
        requireAndThrowProductException(Validator.isNameValid(name)) { InvalidInputException(MemberInvalidInputField.NAME) }
        requireAndThrowProductException(Validator.isEmailValid(email)) { InvalidInputException(MemberInvalidInputField.EMAIL) }
        requireAndThrowProductException(Validator.isPasswordValid(password)) { InvalidInputException(MemberInvalidInputField.PASSWORD) }
    }
}