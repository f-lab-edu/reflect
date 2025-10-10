package kr.co.archan.reflect.unit.auth.dto.response

import kr.co.archan.reflect.auth.dto.response.SignUpResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*

class SignUpResponseTest {

    @Test
    @DisplayName("SignUpResponse 생성 및 프로퍼티 접근")
    fun `SignUpResponse 생성 및 프로퍼티 접근`() {
        // given
        val accessToken = "sample-access-token"
        val refreshToken = "sample-refresh-token"

        // when
        val response = SignUpResponse(accessToken, refreshToken)

        // then
        assertEquals(accessToken, response.accessToken)
        assertEquals(refreshToken, response.refreshToken)
    }

    @Test
    @DisplayName("equals - 같은 값을 가진 SignUpResponse는 동등함")
    fun `equals - 같은 값을 가진 SignUpResponse는 동등함`() {
        // given
        val response1 = SignUpResponse("access-token", "refresh-token")
        val response2 = SignUpResponse("access-token", "refresh-token")

        // when & then
        assertEquals(response1, response2)
        assertTrue(response1 == response2)
    }

    @Test
    @DisplayName("equals - 다른 accessToken을 가진 SignUpResponse는 동등하지 않음")
    fun `equals - 다른 accessToken을 가진 SignUpResponse는 동등하지 않음`() {
        // given
        val response1 = SignUpResponse("access-token-1", "refresh-token")
        val response2 = SignUpResponse("access-token-2", "refresh-token")

        // when & then
        assertNotEquals(response1, response2)
    }

    @Test
    @DisplayName("equals - 다른 refreshToken을 가진 SignUpResponse는 동등하지 않음")
    fun `equals - 다른 refreshToken을 가진 SignUpResponse는 동등하지 않음`() {
        // given
        val response1 = SignUpResponse("access-token", "refresh-token-1")
        val response2 = SignUpResponse("access-token", "refresh-token-2")

        // when & then
        assertNotEquals(response1, response2)
    }

    @Test
    @DisplayName("hashCode - 같은 값을 가진 SignUpResponse는 같은 hashCode")
    fun `hashCode - 같은 값을 가진 SignUpResponse는 같은 hashCode`() {
        // given
        val response1 = SignUpResponse("access-token", "refresh-token")
        val response2 = SignUpResponse("access-token", "refresh-token")

        // when & then
        assertEquals(response1.hashCode(), response2.hashCode())
    }

    @Test
    @DisplayName("hashCode - 다른 값을 가진 SignUpResponse는 다른 hashCode")
    fun `hashCode - 다른 값을 가진 SignUpResponse는 다른 hashCode`() {
        // given
        val response1 = SignUpResponse("access-token-1", "refresh-token-1")
        val response2 = SignUpResponse("access-token-2", "refresh-token-2")

        // when & then
        assertNotEquals(response1.hashCode(), response2.hashCode())
    }

    @Test
    @DisplayName("copy - accessToken 변경")
    fun `copy - accessToken 변경`() {
        // given
        val original = SignUpResponse("original-access", "original-refresh")
        val newAccessToken = "new-access"

        // when
        val copied = original.copy(accessToken = newAccessToken)

        // then
        assertEquals(newAccessToken, copied.accessToken)
        assertEquals(original.refreshToken, copied.refreshToken)
    }

    @Test
    @DisplayName("copy - refreshToken 변경")
    fun `copy - refreshToken 변경`() {
        // given
        val original = SignUpResponse("original-access", "original-refresh")
        val newRefreshToken = "new-refresh"

        // when
        val copied = original.copy(refreshToken = newRefreshToken)

        // then
        assertEquals(original.accessToken, copied.accessToken)
        assertEquals(newRefreshToken, copied.refreshToken)
    }

    @Test
    @DisplayName("copy - 모든 프로퍼티 변경")
    fun `copy - 모든 프로퍼티 변경`() {
        // given
        val original = SignUpResponse("original-access", "original-refresh")
        val newAccessToken = "new-access"
        val newRefreshToken = "new-refresh"

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
        val original = SignUpResponse("access-token", "refresh-token")

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
        val accessToken = "test-access-token"
        val refreshToken = "test-refresh-token"
        val response = SignUpResponse(accessToken, refreshToken)

        // when
        val result = response.toString()

        // then
        assertNotNull(result)
        assertTrue(result.contains("SignUpResponse"))
        assertTrue(result.contains(accessToken))
        assertTrue(result.contains(refreshToken))
    }

    @Test
    @DisplayName("Set에서 중복 제거 - equals와 hashCode 활용")
    fun `Set에서 중복 제거 - equals와 hashCode 활용`() {
        // given
        val response1 = SignUpResponse("access", "refresh")
        val response2 = SignUpResponse("access", "refresh")
        val response3 = SignUpResponse("different-access", "different-refresh")

        // when
        val responseSet = setOf(response1, response2, response3)

        // then
        assertEquals(2, responseSet.size)
        assertTrue(responseSet.contains(response1))
        assertTrue(responseSet.contains(response3))
    }

    @Test
    @DisplayName("빈 문자열 토큰으로 생성 가능")
    fun `빈 문자열 토큰으로 생성 가능`() {
        // given & when
        val response = SignUpResponse("", "")

        // then
        assertEquals("", response.accessToken)
        assertEquals("", response.refreshToken)
    }

    @Test
    @DisplayName("매우 긴 토큰 값 처리")
    fun `매우 긴 토큰 값 처리`() {
        // given
        val longAccessToken = "a".repeat(1000)
        val longRefreshToken = "b".repeat(1000)

        // when
        val response = SignUpResponse(longAccessToken, longRefreshToken)

        // then
        assertEquals(1000, response.accessToken.length)
        assertEquals(1000, response.refreshToken.length)
    }
}