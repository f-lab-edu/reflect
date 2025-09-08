package kr.co.archan.reflect.member.exception.types

import kr.co.archan.reflect.global.exception.types.InvalidInputField

enum class MemberInvalidInputField (override val field: String, override val message: String): InvalidInputField {
    EMAIL("email", "패스워드는 8자 이상 64자 이하로, 숫자와 특수문자(공백 제외)를 포함해야 합니다"),
    NAME("name", "이름 형식이 잘못되었습니다"),
    PASSWORD("password", "이메일 입력이 잘못되었습니다")
}