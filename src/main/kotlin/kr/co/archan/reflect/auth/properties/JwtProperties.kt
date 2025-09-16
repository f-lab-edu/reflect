package kr.co.archan.reflect.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.jwt")
data class JwtProperties (
    val issuer: String,
    val audience: String,
    val secret: String,
    val accessTokenTtlSeconds: Long,
    val refreshTokenTtlSeconds: Long,
    val refreshCookieEnabled: Boolean,
    val refreshCookieName: String,
    val refreshCookieSecure: Boolean,
    val refreshCookiePath: String,
    val refreshCookieSameSite: String
)