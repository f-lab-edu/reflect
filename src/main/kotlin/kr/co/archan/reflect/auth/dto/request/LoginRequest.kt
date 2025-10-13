package kr.co.archan.reflect.auth.dto.request

import kr.co.archan.reflect.global.validation.annotation.ValidEmail
import kr.co.archan.reflect.global.validation.annotation.ValidPassword

data class LoginRequest(
    @field:ValidEmail
    val email: String,

    @field:ValidPassword
    val password: String,
)