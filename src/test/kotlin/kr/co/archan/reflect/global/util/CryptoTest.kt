package kr.co.archan.reflect.global.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

class CryptoTest {

    private lateinit var crypto: Crypto

    @BeforeEach
    fun setUp() {
        crypto = Crypto("test-secret-key")
    }

    @Test
    @DisplayName("sha256 - 기본 문자열 해시 생성")
    fun `sha256 - 기본 문자열 해시 생성`() {
        // given
        val input = "hello world"
        
        // when
        val result = crypto.sha256WithNoSalt(input)
        
        // then
        assertNotNull(result)
        assertFalse(result.isEmpty())
        
        // SHA-256을 Base64 URL 인코딩하면 43자 (32바이트 → 43자, 패딩 없음)
        assertEquals(43, result.length)
        
        // Base64 URL 형식 검증 (패딩 없음)
        assertFalse(result.contains('='))
        assertTrue(result.matches(Regex("^[A-Za-z0-9_-]+$")))
    }

    @Test
    @DisplayName("sha256 - 동일한 입력에 대해 동일한 해시 생성")
    fun `sha256 - 동일한 입력에 대해 동일한 해시 생성`() {
        // given
        val input = "test string"
        
        // when
        val result1 = crypto.sha256WithNoSalt(input)
        val result2 = crypto.sha256WithNoSalt(input)
        
        // then
        assertEquals(result1, result2)
    }

    @Test
    @DisplayName("sha256 - 다른 입력에 대해 다른 해시 생성")
    fun `sha256 - 다른 입력에 대해 다른 해시 생성`() {
        // given
        val input1 = "string1"
        val input2 = "string2"
        
        // when
        val result1 = crypto.sha256WithNoSalt(input1)
        val result2 = crypto.sha256WithNoSalt(input2)
        
        // then
        assertNotEquals(result1, result2)
    }

    @Test
    @DisplayName("sha256 - 빈 문자열 해시 생성")
    fun `sha256 - 빈 문자열 해시 생성`() {
        // given
        val input = ""
        
        // when
        val result = crypto.sha256WithNoSalt(input)
        
        // then
        assertNotNull(result)
        assertEquals(43, result.length)
        
        // 빈 문자열의 SHA-256 해시값은 항상 동일
        val expectedEmpty = crypto.sha256WithNoSalt("")
        assertEquals(expectedEmpty, result)
    }

    @Test
    @DisplayName("sha256 - 긴 문자열 해시 생성")
    fun `sha256 - 긴 문자열 해시 생성`() {
        // given
        val input = "a".repeat(1000) // 1000자 문자열
        
        // when
        val result = crypto.sha256WithNoSalt(input)
        
        // then
        assertNotNull(result)
        assertEquals(43, result.length) // 입력 길이와 상관없이 해시는 항상 43자
    }

    @Test
    @DisplayName("sha256 - 특수문자 포함 문자열 해시 생성")
    fun `sha256 - 특수문자 포함 문자열 해시 생성`() {
        // given
        val input = "!@#$%^&*()_+-=[]{}|;:'\",.<>?/~`"
        
        // when
        val result = crypto.sha256WithNoSalt(input)
        
        // then
        assertNotNull(result)
        assertEquals(43, result.length)
        assertTrue(result.matches(Regex("^[A-Za-z0-9_-]+$")))
    }

    @Test
    @DisplayName("sha256 - 유니코드 문자열 해시 생성")
    fun `sha256 - 유니코드 문자열 해시 생성`() {
        // given
        val input = "안녕하세요 🌟 こんにちは"
        
        // when
        val result = crypto.sha256WithNoSalt(input)
        
        // then
        assertNotNull(result)
        assertEquals(43, result.length)
        assertTrue(result.matches(Regex("^[A-Za-z0-9_-]+$")))
    }

    @Test
    @DisplayName("sha256 - 대소문자 구분 검증")
    fun `sha256 - 대소문자 구분 검증`() {
        // given
        val input1 = "Test"
        val input2 = "test"
        
        // when
        val result1 = crypto.sha256WithNoSalt(input1)
        val result2 = crypto.sha256WithNoSalt(input2)
        
        // then
        assertNotEquals(result1, result2)
    }

    @Test
    @DisplayName("sha256 - 공백 포함 문자열 해시 생성")
    fun `sha256 - 공백 포함 문자열 해시 생성`() {
        // given
        val input1 = "hello world"
        val input2 = "helloworld"
        val input3 = " hello world "
        
        // when
        val result1 = crypto.sha256WithNoSalt(input1)
        val result2 = crypto.sha256WithNoSalt(input2)
        val result3 = crypto.sha256WithNoSalt(input3)
        
        // then
        // 모두 다른 해시값이어야 함 (공백도 해시에 영향)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertNotEquals(result2, result3)
    }

    @Test
    @DisplayName("sha256 - Base64 URL 인코딩 형식 검증")
    fun `sha256 - Base64 URL 인코딩 형식 검증`() {
        // given
        val inputs = listOf("test1", "test2", "test3", "very_long_string_for_testing")
        
        inputs.forEach { input ->
            // when
            val result = crypto.sha256WithNoSalt(input)
            
            // then
            // Base64 URL 인코딩은 A-Z, a-z, 0-9, -, _ 만 사용 (패딩 없음)
            assertTrue(result.matches(Regex("^[A-Za-z0-9_-]+$")), "Input: $input, Result: $result")
            assertFalse(result.contains('+'), "Input: $input should not contain '+'")
            assertFalse(result.contains('/'), "Input: $input should not contain '/'")
            assertFalse(result.contains('='), "Input: $input should not contain padding '='")
            assertEquals(43, result.length, "Input: $input should produce 43 character hash")
        }
    }
}
