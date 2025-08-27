package kr.co.archan.reflect.member.dto.request

import kr.co.archan.reflect.global.util.Validator
import kr.co.archan.reflect.global.util.extension.requireAndThrowProductException
import kr.co.archan.reflect.global.vo.Email
import kr.co.archan.reflect.member.dto.command.MemberSignupCommand
import kr.co.archan.reflect.member.exception.InvalidEmailException
import kr.co.archan.reflect.member.exception.InvalidNameException

data class MemberSignupRequest(
    val email: String,
    val password: String,
    val name: String
) {
    fun toCommand(): MemberSignupCommand {

        requireAndThrowProductException(Validator.isNameValid(name)) { InvalidNameException() }
        requireAndThrowProductException(Validator.isNameValid(email)) { InvalidEmailException() }

        return MemberSignupCommand(
            email = Email.of(email),
            password = password,
            name = name
        )
    }
}