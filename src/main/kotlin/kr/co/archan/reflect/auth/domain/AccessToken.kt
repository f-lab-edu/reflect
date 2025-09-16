package kr.co.archan.reflect.auth.domain

import java.time.Instant

data class AccessToken (
    val value: String,
    val expiresAt: Instant,
)