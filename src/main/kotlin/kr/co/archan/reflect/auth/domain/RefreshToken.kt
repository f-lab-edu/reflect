package kr.co.archan.reflect.auth.domain

import java.time.Instant

data class RefreshToken (
    val value: String,
    val memberId: String,
    val expiresAt: Instant
)