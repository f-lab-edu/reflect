package kr.co.archan.reflect.auth.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)
