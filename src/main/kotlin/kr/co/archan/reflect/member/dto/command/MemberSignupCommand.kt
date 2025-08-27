package kr.co.archan.reflect.member.dto.command

import kr.co.archan.reflect.global.vo.Email

data class MemberSignupCommand(
    val email: Email,
    val password: String,
    val name: String
)