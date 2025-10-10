package kr.co.archan.reflect.unit.auth.provider

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import kr.co.archan.reflect.auth.domain.RefreshToken
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.math.abs

class RefreshTokenProviderTest {

    @Test
    @DisplayName("provideRefreshToken - 리프레시 토큰 생성 기본 기능")
    fun `provideRefreshToken - 리프레시 토큰 생성 기본 기능`() {
        // given
        val jwtProperties = mockk<JwtProperties> {
            every { refreshTokenTtlSeconds } returns 604800L // 7일
        }
        
        val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
        val tokenSlot = slot<RefreshToken>()
        every { refreshTokenRepository.save(capture(tokenSlot)) } returns mockk()
        
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        val memberId = 12345L
        val beforeCall = Instant.now()
        
        // when
        val result = refreshTokenProvider.provideRefreshToken(memberId)
        val afterCall = Instant.now()
        
        // then
        // RefreshToken 객체가 정상적으로 반환되는지
        assertNotNull(result)
        
        // 생성된 토큰 값이 null이 아니고 비어있지 않은지
        assertNotNull(result.value)
        assertFalse(result.value.isEmpty())
        assertFalse(result.value.isBlank())
        
        // memberId가 올바르게 설정되었는지
        assertEquals(memberId.toString(), result.memberId)
        
        // 만료시간이 현재 시간보다 미래인지
        assertTrue(result.expiresAt.isAfter(beforeCall))
        assertTrue(result.expiresAt.isAfter(afterCall))
        
        // 만료시간이 대략적으로 현재 시간 + TTL과 일치하는지 (2초 이내 오차 허용)
        val expectedExpiry = beforeCall.plusSeconds(604800L)
        val timeDifference = abs(result.expiresAt.epochSecond - expectedExpiry.epochSecond)
        assertTrue(timeDifference <= 2)
        
        // repository.save가 호출되었는지 검증
        verify(exactly = 1) { refreshTokenRepository.save(any()) }
        
        // 저장된 토큰이 반환된 토큰과 동일한지 검증
        val savedToken = tokenSlot.captured
        assertEquals(result.value, savedToken.value)
        assertEquals(result.memberId, savedToken.memberId)
        assertEquals(result.expiresAt, savedToken.expiresAt)
    }

    @Test
    @DisplayName("provideRefreshToken - 랜덤 토큰 생성 검증")
    fun `provideRefreshToken - 랜덤 토큰 생성 검증`() {
        // given
        val jwtProperties = mockk<JwtProperties> {
            every { refreshTokenTtlSeconds } returns 604800L
        }
        
        val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
        every { refreshTokenRepository.save(any()) } returns mockk()
        
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        val memberId = 12345L
        
        // when
        val token1 = refreshTokenProvider.provideRefreshToken(memberId)
        val token2 = refreshTokenProvider.provideRefreshToken(memberId)
        
        // then
        // 동일한 memberId라도 매번 다른 토큰이 생성되어야 함
        assertNotEquals(token1.value, token2.value)
        
        // 토큰 길이가 일정해야 함 (32바이트를 Base64 URL 인코딩하면 43자)
        assertEquals(43, token1.value.length)
        assertEquals(43, token2.value.length)
        
        // Base64 URL 인코딩 형식인지 확인 (패딩 없음)
        assertFalse(token1.value.contains('='))
        assertFalse(token2.value.contains('='))
        assertTrue(token1.value.matches(Regex("^[A-Za-z0-9_-]+$")))
        assertTrue(token2.value.matches(Regex("^[A-Za-z0-9_-]+$")))
        
        // repository.save가 2번 호출되었는지 검증
        verify(exactly = 2) { refreshTokenRepository.save(any()) }
    }

    @Test
    @DisplayName("provideRefreshToken - 다른 memberId로 토큰 생성")
    fun `provideRefreshToken - 다른 memberId로 토큰 생성`() {
        // given
        val jwtProperties = mockk<JwtProperties> {
            every { refreshTokenTtlSeconds } returns 604800L
        }
        
        val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
        every { refreshTokenRepository.save(any()) } returns mockk()
        
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        val memberId1 = 12345L
        val memberId2 = 67890L
        
        // when
        val token1 = refreshTokenProvider.provideRefreshToken(memberId1)
        val token2 = refreshTokenProvider.provideRefreshToken(memberId2)
        
        // then
        // 토큰 값은 다르지만 (랜덤 생성이므로)
        assertNotEquals(token1.value, token2.value)
        
        // memberId는 각각 올바르게 설정되어야 함
        assertEquals(memberId1.toString(), token1.memberId)
        assertEquals(memberId2.toString(), token2.memberId)
        
        // 만료시간은 비슷해야 함 (거의 동시에 생성되었으므로)
        val timeDifference = abs(token1.expiresAt.epochSecond - token2.expiresAt.epochSecond)
        assertTrue(timeDifference <= 1)
        
        // repository.save가 2번 호출되었는지 검증
        verify(exactly = 2) { refreshTokenRepository.save(any()) }
    }

    @Test
    @DisplayName("provideRefreshToken - TTL 오버플로우로 토큰 발급 실패")
    fun `provideRefreshToken - TTL 오버플로우로 토큰 발급 실패`() {
        // given
        val jwtProperties = mockk<JwtProperties> {
            every { refreshTokenTtlSeconds } returns Long.MAX_VALUE // ← 오버플로우 유발
        }
        
        val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
        
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        val memberId = 12345L
        
        // when & then
        assertThrows<ArithmeticException> {
            refreshTokenProvider.provideRefreshToken(memberId)
        }
        
        // 오버플로우로 인해 save가 호출되지 않았는지 검증
        verify(exactly = 0) { refreshTokenRepository.save(any()) }
    }
}
