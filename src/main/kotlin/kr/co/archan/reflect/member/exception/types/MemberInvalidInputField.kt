package kr.co.archan.reflect.member.exception.types

import kr.co.archan.reflect.global.exception.types.InvalidInputErrorSpec

enum class MemberInvalidInputField(
    override val invalidField: String,
    override val systemMessage: String,
    override val userMessage: String,
): InvalidInputErrorSpec {
    EMAIL(
        invalidField = "email",
        systemMessage = "Email validation failed - invalid format or length",
        userMessage = "올바른 이메일 형식을 입력해주세요",
    ),
    NAME(
        invalidField = "name",
        systemMessage = "Name validation failed - invalid length or format",
        userMessage = "이름은 1자 이상 20자 이하로 입력해주세요",
    ),
    PASSWORD(
        invalidField = "password",
        systemMessage = "Password validation failed - invalid length or missing required characters",
        userMessage = "패스워드는 8자 이상 64자 이하로, 숫자와 특수문자를 포함해야 합니다",
    ),
    UNKNOWN(
        invalidField = "unknown",
        systemMessage = "Unknown invalid field",
        userMessage = "입력값을 다시 확인해주세요",
    )
}