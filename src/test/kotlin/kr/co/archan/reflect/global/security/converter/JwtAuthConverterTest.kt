package kr.co.archan.reflect.global.security.converter

import io.mockk.every
import io.mockk.mockk
import kr.co.archan.reflect.auth.domain.MemberPrincipal
import kr.co.archan.reflect.auth.exception.common.AuthException
import kr.co.archan.reflect.auth.exception.types.AuthErrorCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JwtAuthConverterTest {

    private val jwtAuthConverter = JwtAuthConverter()

    @Test
    @DisplayName("JWT 변환 - 기본 권한 (roles 없음)")
    fun `JWT 변환 - 기본 권한 (roles 없음)`() {
        // given
        val jwt = mockk<Jwt> {
            every { getClaimAsString("member_id") } returns "12345"
            every { getClaimAsString("email") } returns "test@example.com"
            every { claims } returns mapOf(
                "member_id" to "12345",
                "email" to "test@example.com"
            )
        }
        
        // when
        val result = jwtAuthConverter.convert(jwt)
        
        // then
        assertNotNull(result)
        assertTrue(result is UsernamePasswordAuthenticationToken)
        
        // Principal 검증
        val principal = result.principal as MemberPrincipal
        assertEquals(12345L, principal.memberId)
        assertEquals("test@example.com", principal.email)
        
        // 기본 권한 검증
        assertEquals(1, result.authorities.size)
        assertTrue(result.authorities.contains(SimpleGrantedAuthority("ROLE_USER")))
        
        // Credentials 검증
        assertEquals("N/A", result.credentials)
    }

    @Test
    @DisplayName("JWT 변환 - 커스텀 권한 (roles 있음)")
    fun `JWT 변환 - 커스텀 권한 (roles 있음)`() {
        // given
        val jwt = mockk<Jwt> {
            every { getClaimAsString("member_id") } returns "67890"
            every { getClaimAsString("email") } returns "admin@example.com"
            every { claims } returns mapOf(
                "member_id" to "67890",
                "email" to "admin@example.com",
                "roles" to listOf("ADMIN", "USER")
            )
        }
        
        // when
        val result = jwtAuthConverter.convert(jwt)
        
        // then
        assertNotNull(result)
        
        // Principal 검증
        val principal = result.principal as MemberPrincipal
        assertEquals(67890L, principal.memberId)
        assertEquals("admin@example.com", principal.email)
        
        // 커스텀 권한 검증
        assertEquals(2, result.authorities.size)
        assertTrue(result.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")))
        assertTrue(result.authorities.contains(SimpleGrantedAuthority("ROLE_USER")))
    }

    @Test
    @DisplayName("JWT 변환 실패 - member_id 클레임 누락")
    fun `JWT 변환 실패 - member_id 클레임 누락`() {
        // given
        val jwt = mockk<Jwt> {
            every { getClaimAsString("member_id") } returns null
            every { getClaimAsString("email") } returns "test@example.com"
            every { claims } returns mapOf("email" to "test@example.com")
        }
        
        // when & then
        val exception = assertThrows<AuthException> {
            jwtAuthConverter.convert(jwt)
        }
        
        // ErrorCode 검증
        assertEquals(AuthErrorCode.JWT_REQUIRED_CLAIM_MISSING, exception.apiErrorSpec)
    }

    @Test
    @DisplayName("JWT 변환 실패 - email 클레임 누락")
    fun `JWT 변환 실패 - email 클레임 누락`() {
        // given
        val jwt = mockk<Jwt> {
            every { getClaimAsString("member_id") } returns "12345"
            every { getClaimAsString("email") } returns null
            every { claims } returns mapOf("member_id" to "12345")
        }
        
        // when & then
        val exception = assertThrows<AuthException> {
            jwtAuthConverter.convert(jwt)
        }
        
        // ErrorCode 검증
        assertEquals(AuthErrorCode.JWT_REQUIRED_CLAIM_MISSING, exception.apiErrorSpec)
    }

    @Test
    @DisplayName("JWT 변환 실패 - member_id 타입 변환 실패")
    fun `JWT 변환 실패 - member_id 타입 변환 실패`() {
        // given
        val jwt = mockk<Jwt> {
            every { getClaimAsString("member_id") } returns "invalid_number"
            every { getClaimAsString("email") } returns "test@example.com"
            every { claims } returns mapOf(
                "member_id" to "invalid_number",
                "email" to "test@example.com"
            )
        }
        
        // when & then
        val exception = assertThrows<AuthException> {
            jwtAuthConverter.convert(jwt)
        }
        
        // ErrorCode 검증
        assertEquals(AuthErrorCode.JWT_CLAIM_FORMAT_INVALID, exception.apiErrorSpec)
    }

    @Test
    @DisplayName("JWT 변환 - 다른 입력값으로 다른 Principal 생성")
    fun `JWT 변환 - 다른 입력값으로 다른 Principal 생성`() {
        // given
        val jwt1 = mockk<Jwt> {
            every { getClaimAsString("member_id") } returns "11111"
            every { getClaimAsString("email") } returns "user1@example.com"
            every { claims } returns mapOf(
                "member_id" to "11111",
                "email" to "user1@example.com"
            )
        }
        
        val jwt2 = mockk<Jwt> {
            every { getClaimAsString("member_id") } returns "22222"
            every { getClaimAsString("email") } returns "user2@example.com"
            every { claims } returns mapOf(
                "member_id" to "22222",
                "email" to "user2@example.com"
            )
        }
        
        // when
        val result1 = jwtAuthConverter.convert(jwt1)
        val result2 = jwtAuthConverter.convert(jwt2)
        
        // then
        val principal1 = result1.principal as MemberPrincipal
        val principal2 = result2.principal as MemberPrincipal
        
        // 각각 다른 Principal 생성 검증
        assertNotEquals(principal1.memberId, principal2.memberId)
        assertNotEquals(principal1.email, principal2.email)
        
        // 각각의 값 정확성 검증
        assertEquals(11111L, principal1.memberId)
        assertEquals("user1@example.com", principal1.email)
        assertEquals(22222L, principal2.memberId)
        assertEquals("user2@example.com", principal2.email)
        
        // 둘 다 기본 권한 가져야 함
        assertEquals(1, result1.authorities.size)
        assertEquals(1, result2.authorities.size)
        assertTrue(result1.authorities.contains(SimpleGrantedAuthority("ROLE_USER")))
        assertTrue(result2.authorities.contains(SimpleGrantedAuthority("ROLE_USER")))
    }
}
