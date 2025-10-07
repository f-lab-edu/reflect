package kr.co.archan.reflect.auth.dto.vo

import kr.co.archan.reflect.auth.domain.AccessToken
import kr.co.archan.reflect.auth.domain.RefreshToken
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import java.time.Instant

class AuthTokenTest {

    @Test
    @DisplayName("AuthToken 생성 및 프로퍼티 접근")
    fun `AuthToken 생성 및 프로퍼티 접근`() {
        // given
        val accessToken = AccessToken("access-token-value", Instant.now().plusSeconds(3600))
        val refreshToken = RefreshToken("refresh-token-value", "12345", Instant.now().plusSeconds(1209600))

        // when
        val authToken = AuthToken(accessToken, refreshToken)

        // then
        assertEquals(accessToken, authToken.accessToken)
        assertEquals(refreshToken, authToken.refreshToken)
    }

    @Test
    @DisplayName("equals - 같은 값을 가진 AuthToken은 동등함")
    fun `equals - 같은 값을 가진 AuthToken은 동등함`() {
        // given
        val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
        val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
        
        val authToken1 = AuthToken(accessToken, refreshToken)
        val authToken2 = AuthToken(accessToken, refreshToken)

        // when & then
        assertEquals(authToken1, authToken2)
        assertTrue(authToken1 == authToken2)
    }

    @Test
    @DisplayName("equals - 다른 accessToken을 가진 AuthToken은 동등하지 않음")
    fun `equals - 다른 accessToken을 가진 AuthToken은 동등하지 않음`() {
        // given
        val accessToken1 = AccessToken("access1", Instant.now())
        val accessToken2 = AccessToken("access2", Instant.now())
        val refreshToken = RefreshToken("refresh", "100", Instant.now())
        
        val authToken1 = AuthToken(accessToken1, refreshToken)
        val authToken2 = AuthToken(accessToken2, refreshToken)

        // when & then
        assertNotEquals(authToken1, authToken2)
    }

    @Test
    @DisplayName("equals - 다른 refreshToken을 가진 AuthToken은 동등하지 않음")
    fun `equals - 다른 refreshToken을 가진 AuthToken은 동등하지 않음`() {
        // given
        val accessToken = AccessToken("access", Instant.now())
        val refreshToken1 = RefreshToken("refresh1", "100", Instant.now())
        val refreshToken2 = RefreshToken("refresh2", "100", Instant.now())
        
        val authToken1 = AuthToken(accessToken, refreshToken1)
        val authToken2 = AuthToken(accessToken, refreshToken2)

        // when & then
        assertNotEquals(authToken1, authToken2)
    }

    @Test
    @DisplayName("hashCode - 같은 값을 가진 AuthToken은 같은 hashCode")
    fun `hashCode - 같은 값을 가진 AuthToken은 같은 hashCode`() {
        // given
        val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
        val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
        
        val authToken1 = AuthToken(accessToken, refreshToken)
        val authToken2 = AuthToken(accessToken, refreshToken)

        // when & then
        assertEquals(authToken1.hashCode(), authToken2.hashCode())
    }

    @Test
    @DisplayName("hashCode - 다른 값을 가진 AuthToken은 다른 hashCode")
    fun `hashCode - 다른 값을 가진 AuthToken은 다른 hashCode`() {
        // given
        val accessToken1 = AccessToken("access1", Instant.now())
        val accessToken2 = AccessToken("access2", Instant.now())
        val refreshToken1 = RefreshToken("refresh1", "100", Instant.now())
        val refreshToken2 = RefreshToken("refresh2", "100", Instant.now())
        
        val authToken1 = AuthToken(accessToken1, refreshToken1)
        val authToken2 = AuthToken(accessToken2, refreshToken2)

        // when & then
        assertNotEquals(authToken1.hashCode(), authToken2.hashCode())
    }

    @Test
    @DisplayName("copy - accessToken 변경")
    fun `copy - accessToken 변경`() {
        // given
        val originalAccessToken = AccessToken("original-access", Instant.now())
        val newAccessToken = AccessToken("new-access", Instant.now().plusSeconds(7200))
        val refreshToken = RefreshToken("refresh", "100", Instant.now())
        val original = AuthToken(originalAccessToken, refreshToken)

        // when
        val copied = original.copy(accessToken = newAccessToken)

        // then
        assertEquals(newAccessToken, copied.accessToken)
        assertEquals(original.refreshToken, copied.refreshToken)
        assertNotEquals(original.accessToken, copied.accessToken)
    }

    @Test
    @DisplayName("copy - refreshToken 변경")
    fun `copy - refreshToken 변경`() {
        // given
        val accessToken = AccessToken("access", Instant.now())
        val originalRefreshToken = RefreshToken("original-refresh", "100", Instant.now())
        val newRefreshToken = RefreshToken("new-refresh", "200", Instant.now().plusSeconds(14400))
        val original = AuthToken(accessToken, originalRefreshToken)

        // when
        val copied = original.copy(refreshToken = newRefreshToken)

        // then
        assertEquals(original.accessToken, copied.accessToken)
        assertEquals(newRefreshToken, copied.refreshToken)
        assertNotEquals(original.refreshToken, copied.refreshToken)
    }

    @Test
    @DisplayName("copy - 모든 프로퍼티 변경")
    fun `copy - 모든 프로퍼티 변경`() {
        // given
        val originalAccessToken = AccessToken("original-access", Instant.now())
        val originalRefreshToken = RefreshToken("original-refresh", "100", Instant.now())
        val newAccessToken = AccessToken("new-access", Instant.now().plusSeconds(3600))
        val newRefreshToken = RefreshToken("new-refresh", "200", Instant.now().plusSeconds(7200))
        val original = AuthToken(originalAccessToken, originalRefreshToken)

        // when
        val copied = original.copy(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )

        // then
        assertEquals(newAccessToken, copied.accessToken)
        assertEquals(newRefreshToken, copied.refreshToken)
        assertNotEquals(original, copied)
    }

    @Test
    @DisplayName("copy - 모든 프로퍼티 복사")
    fun `copy - 모든 프로퍼티 복사`() {
        // given
        val accessToken = AccessToken("access", Instant.now())
        val refreshToken = RefreshToken("refresh", "100", Instant.now())
        val original = AuthToken(accessToken, refreshToken)

        // when
        val copied = original.copy()

        // then
        assertEquals(original, copied)
        assertNotSame(original, copied)
    }

    @Test
    @DisplayName("toString - 문자열 표현 포함")
    fun `toString - 문자열 표현 포함`() {
        // given
        val accessToken = AccessToken("test-access", Instant.parse("2025-10-06T12:00:00Z"))
        val refreshToken = RefreshToken("test-refresh", "999", Instant.parse("2025-10-20T12:00:00Z"))
        val authToken = AuthToken(accessToken, refreshToken)

        // when
        val result = authToken.toString()

        // then
        assertNotNull(result)
        assertTrue(result.contains("AuthToken"))
        assertTrue(result.contains("AccessToken"))
        assertTrue(result.contains("RefreshToken"))
    }

    @Test
    @DisplayName("Set에서 중복 제거 - equals와 hashCode 활용")
    fun `Set에서 중복 제거 - equals와 hashCode 활용`() {
        // given
        val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
        val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
        
        val authToken1 = AuthToken(accessToken, refreshToken)
        val authToken2 = AuthToken(accessToken, refreshToken)
        val authToken3 = AuthToken(
            AccessToken("different-access", Instant.now()),
            RefreshToken("different-refresh", "200", Instant.now())
        )

        // when
        val tokenSet = setOf(authToken1, authToken2, authToken3)

        // then
        assertEquals(2, tokenSet.size)
        assertTrue(tokenSet.contains(authToken1))
        assertTrue(tokenSet.contains(authToken3))
    }

    @Test
    @DisplayName("Map의 키로 사용 - equals와 hashCode 활용")
    fun `Map의 키로 사용 - equals와 hashCode 활용`() {
        // given
        val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
        val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
        
        val authToken1 = AuthToken(accessToken, refreshToken)
        val authToken2 = AuthToken(accessToken, refreshToken)
        
        val map = mutableMapOf<AuthToken, String>()

        // when
        map[authToken1] = "first"
        map[authToken2] = "second"

        // then
        assertEquals(1, map.size)
        assertEquals("second", map[authToken1])
        assertEquals("second", map[authToken2])
    }

    @Test
    @DisplayName("AccessToken과 RefreshToken의 만료 시간이 독립적으로 관리됨")
    fun `AccessToken과 RefreshToken의 만료 시간이 독립적으로 관리됨`() {
        // given
        val now = Instant.now()
        val accessExpiry = now.plusSeconds(3600)
        val refreshExpiry = now.plusSeconds(1209600)
        
        val accessToken = AccessToken("access", accessExpiry)
        val refreshToken = RefreshToken("refresh", "100", refreshExpiry)
        val authToken = AuthToken(accessToken, refreshToken)

        // when & then
        assertTrue(authToken.refreshToken.expiresAt.isAfter(authToken.accessToken.expiresAt))
        assertEquals(accessExpiry, authToken.accessToken.expiresAt)
        assertEquals(refreshExpiry, authToken.refreshToken.expiresAt)
    }

    @Test
    @DisplayName("서로 다른 memberId를 가진 AuthToken은 구분됨")
    fun `서로 다른 memberId를 가진 AuthToken은 구분됨`() {
        // given
        val accessToken = AccessToken("access", Instant.now())
        val refreshToken1 = RefreshToken("refresh", "100", Instant.now())
        val refreshToken2 = RefreshToken("refresh", "200", Instant.now())
        
        val authToken1 = AuthToken(accessToken, refreshToken1)
        val authToken2 = AuthToken(accessToken, refreshToken2)

        // when & then
        assertNotEquals(authToken1, authToken2)
        assertEquals("100", authToken1.refreshToken.memberId)
        assertEquals("200", authToken2.refreshToken.memberId)
    }
}

