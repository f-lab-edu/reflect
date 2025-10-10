package kr.co.archan.reflect.auth.provider

import kr.co.archan.reflect.auth.domain.RefreshToken
import kr.co.archan.reflect.auth.properties.JwtProperties
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Instant
import java.util.*

@Component
class RefreshTokenProvider (
    private val properties: JwtProperties
){
    private val secureRandom = SecureRandom()

    fun provideRefreshToken(memberId: Long): RefreshToken {
        val value = randomToken()
        val expiresAt = Instant.now().plusSeconds(properties.refreshTokenTtlSeconds)
        return RefreshToken(
            value = value,
            memberId = memberId.toString(),
            expiresAt = expiresAt
        )
    }

    private fun randomToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

}