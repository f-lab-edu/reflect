package kr.co.archan.reflect.auth.provider

import com.nimbusds.jwt.SignedJWT
import io.mockk.every
import io.mockk.mockk
import kr.co.archan.reflect.auth.properties.JwtProperties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.math.abs

class AccessTokenProviderTest {

    @Test
    @DisplayName("provideAccessToken - JWT 토큰 생성 기본 기능")
    fun `provideAccessToken - JWT 토큰 생성 기본 기능`() {
        // given
        val jwtProperties = mockk<JwtProperties> {
            every { issuer } returns "test-issuer"
            every { audience } returns "test-audience"
            every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough"
            every { accessTokenTtlSeconds } returns 3600L
        }
        
        val accessTokenProvider = AccessTokenProvider(jwtProperties)
        val memberId = 12345L
        val email = "test@example.com"
        val beforeCall = Instant.now()
        
        // when
        val result = accessTokenProvider.provideAccessToken(memberId, email)
        val afterCall = Instant.now()
        
        // then
        assertNotNull(result)
        
        // 생성된 토큰 값이 null이 아니고 비어있지 않은지
        assertNotNull(result.value)
        assertFalse(result.value.isEmpty())
        assertFalse(result.value.isBlank())
        
        // 만료시간이 현재 시간보다 미래인지
        assertTrue(result.expiresAt.isAfter(beforeCall))
        assertTrue(result.expiresAt.isAfter(afterCall))
        
        // 만료시간이 대략적으로 현재 시간 + TTL과 일치하는지, 테스트 실행 시간 고려하여 2초이내의 오차 허용
        val expectedExpiry = beforeCall.plusSeconds(3600L)
        val timeDifference = abs(result.expiresAt.epochSecond - expectedExpiry.epochSecond)
        assertTrue(timeDifference <= 2) // 2초 이내 오차 허용
        
        // JWT 토큰을 파싱해서 클레임 검증
        val signedJWT = SignedJWT.parse(result.value)
        val claims = signedJWT.jwtClaimsSet
        
        // 토큰에 올바른 사용자 정보가 포함되어 있는지 검증
        assertEquals(memberId.toString(), claims.subject)
        assertEquals(email, claims.getStringClaim("email"))
        assertEquals(memberId.toString(), claims.getStringClaim("member_id"))
        assertEquals("test-issuer", claims.issuer)
        assertEquals(listOf("test-audience"), claims.audience)
    }

    @Test
    @DisplayName("provideAccessToken - TTL 오버플로우로 토큰 발급 실패")
    fun `provideAccessToken - TTL 오버플로우로 토큰 발급 실패`() {
        // given
        val jwtProperties = mockk<JwtProperties> {
            every { issuer } returns "test-issuer"
            every { audience } returns "test-audience"
            every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough"
            every { accessTokenTtlSeconds } returns Long.MAX_VALUE // ← 오버플로우 유발
        }
        
        val accessTokenProvider = AccessTokenProvider(jwtProperties)
        val memberId = 12345L
        val email = "test@example.com"
        
        // when & then
        assertThrows<ArithmeticException> {
            accessTokenProvider.provideAccessToken(memberId, email)
        }
    }
}


