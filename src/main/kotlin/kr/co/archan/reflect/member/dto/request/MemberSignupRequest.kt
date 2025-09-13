package kr.co.archan.reflect.member.dto.request

import kr.co.archan.reflect.global.validation.annotation.ValidEmail
import kr.co.archan.reflect.global.validation.annotation.ValidName
import kr.co.archan.reflect.global.validation.annotation.ValidPassword

data class MemberSignupRequest(
    @field:ValidEmail
    val email: String,
    
    @field:ValidPassword
    val password: String,
    
    @field:ValidName
    val name: String
)