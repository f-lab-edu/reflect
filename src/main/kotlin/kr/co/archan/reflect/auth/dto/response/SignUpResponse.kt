package kr.co.archan.reflect.auth.dto.response

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String,
)
