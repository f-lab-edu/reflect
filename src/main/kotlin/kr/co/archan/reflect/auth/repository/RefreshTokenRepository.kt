package kr.co.archan.reflect.auth.repository

import kr.co.archan.reflect.auth.domain.RefreshToken
import kr.co.archan.reflect.global.util.Crypto
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.Duration
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenRepository (
    private val stringRedisTemplate: StringRedisTemplate
){
    private fun key(value: String) = "rt:${Crypto.sha256(value)}"

    fun save(token: RefreshToken) {
        val ttl = Duration.between(Instant.now(), token.expiresAt)
        stringRedisTemplate.opsForValue().set(key(token.value), token.memberId, ttl)
    }

    fun findByValue(value: String): RefreshToken? {
        val memberId = stringRedisTemplate.opsForValue().get(key(value)) ?: return null
        val expireAt = stringRedisTemplate.getExpire(key(value), TimeUnit.SECONDS)
        return RefreshToken(value, memberId, Instant.now().plusSeconds(expireAt))
    }

    fun delete(value: String) {
        stringRedisTemplate.delete(key(value))
    }
}