package kr.co.archan.reflect.auth.dto.vo

import kr.co.archan.reflect.auth.domain.AccessToken
import kr.co.archan.reflect.auth.domain.RefreshToken

data class AuthToken (
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
)