package kr.co.archan.reflect.unit.member.dto.vo

import kr.co.archan.reflect.member.dto.vo.HashedPassword
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*

class HashedPasswordTest {

    @Test
    @DisplayName("HashedPassword 생성 및 프로퍼티 접근")
    fun `HashedPassword 생성 및 프로퍼티 접근`() {
        // given
        val passwordValue = "hashedPassword123"

        // when
        val hashedPassword = HashedPassword(passwordValue)

        // then
        assertEquals(passwordValue, hashedPassword.value)
    }

    @Test
    @DisplayName("equals - 같은 값을 가진 HashedPassword는 동등함")
    fun `equals - 같은 값을 가진 HashedPassword는 동등함`() {
        // given
        val password1 = HashedPassword("same-hashed-password")
        val password2 = HashedPassword("same-hashed-password")

        // when & then
        assertEquals(password1, password2)
        assertTrue(password1 == password2)
    }

    @Test
    @DisplayName("equals - 다른 값을 가진 HashedPassword는 동등하지 않음")
    fun `equals - 다른 값을 가진 HashedPassword는 동등하지 않음`() {
        // given
        val password1 = HashedPassword("hashed-password-1")
        val password2 = HashedPassword("hashed-password-2")

        // when & then
        assertNotEquals(password1, password2)
        assertFalse(password1 == password2)
    }

    @Test
    @DisplayName("hashCode - 같은 값을 가진 HashedPassword는 같은 hashCode")
    fun `hashCode - 같은 값을 가진 HashedPassword는 같은 hashCode`() {
        // given
        val password1 = HashedPassword("hashed-password")
        val password2 = HashedPassword("hashed-password")

        // when & then
        assertEquals(password1.hashCode(), password2.hashCode())
    }

    @Test
    @DisplayName("hashCode - 다른 값을 가진 HashedPassword는 다른 hashCode")
    fun `hashCode - 다른 값을 가진 HashedPassword는 다른 hashCode`() {
        // given
        val password1 = HashedPassword("hashed-password-1")
        val password2 = HashedPassword("hashed-password-2")

        // when & then
        assertNotEquals(password1.hashCode(), password2.hashCode())
    }

    @Test
    @DisplayName("toString - 문자열 표현 포함")
    fun `toString - 문자열 표현 포함`() {
        // given
        val passwordValue = "hashed-password-value"
        val hashedPassword = HashedPassword(passwordValue)

        // when
        val result = hashedPassword.toString()

        // then
        assertNotNull(result)
        assertTrue(result.contains("HashedPassword") || result.contains(passwordValue))
    }

    @Test
    @DisplayName("빈 문자열로 HashedPassword 생성 가능")
    fun `빈 문자열로 HashedPassword 생성 가능`() {
        // given & when
        val hashedPassword = HashedPassword("")

        // then
        assertEquals("", hashedPassword.value)
    }

    @Test
    @DisplayName("매우 긴 해시 값 처리")
    fun `매우 긴 해시 값 처리`() {
        // given
        val longHashValue = "a".repeat(1000)

        // when
        val hashedPassword = HashedPassword(longHashValue)

        // then
        assertEquals(1000, hashedPassword.value.length)
        assertEquals(longHashValue, hashedPassword.value)
    }

    @Test
    @DisplayName("Set에서 중복 제거 - equals와 hashCode 활용")
    fun `Set에서 중복 제거 - equals와 hashCode 활용`() {
        // given
        val password1 = HashedPassword("hashed-password")
        val password2 = HashedPassword("hashed-password")
        val password3 = HashedPassword("different-hashed-password")

        // when
        val passwordSet = setOf(password1, password2, password3)

        // then
        assertEquals(2, passwordSet.size)
        assertTrue(passwordSet.contains(password1))
        assertTrue(passwordSet.contains(password3))
    }

    @Test
    @DisplayName("Map의 키로 사용 가능")
    fun `Map의 키로 사용 가능`() {
        // given
        val password1 = HashedPassword("key-password-1")
        val password2 = HashedPassword("key-password-2")
        val map = mutableMapOf<HashedPassword, String>()

        // when
        map[password1] = "value1"
        map[password2] = "value2"

        // then
        assertEquals(2, map.size)
        assertEquals("value1", map[password1])
        assertEquals("value2", map[password2])
    }

    @Test
    @DisplayName("동일한 키로 Map 값 덮어쓰기")
    fun `동일한 키로 Map 값 덮어쓰기`() {
        // given
        val password1 = HashedPassword("same-key")
        val password2 = HashedPassword("same-key")
        val map = mutableMapOf<HashedPassword, String>()

        // when
        map[password1] = "value1"
        map[password2] = "value2"

        // then
        assertEquals(1, map.size)
        assertEquals("value2", map[password1])
        assertEquals("value2", map[password2])
    }

}