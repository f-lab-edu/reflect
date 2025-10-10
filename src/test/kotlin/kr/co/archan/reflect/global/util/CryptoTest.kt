package kr.co.archan.reflect.global.util

import kr.co.archan.reflect.global.properties.CryptoProperties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class CryptoTest {

    private lateinit var crypto: Crypto
    private lateinit var passwordEncoder: PasswordEncoder
    private val testPepperKey = "test-pepper-key"

    @BeforeEach
    fun setUp() {
        val cryptoProperties = CryptoProperties(
            hashKey = "test-secret-key",
            pepperKey = testPepperKey
        )
        // 실제 Argon2PasswordEncoder 인스턴스 사용
        passwordEncoder = Argon2PasswordEncoder(16, 32, 1, 64 * 1024, 3)
        crypto = Crypto(cryptoProperties, passwordEncoder)
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

    @Test
    @DisplayName("hashPassword - 비밀번호 해싱이 정상 작동")
    fun `hashPassword - 비밀번호 해싱이 정상 작동`() {
        // given
        val password = "myPassword123"
        
        // when
        val result = crypto.hashPassword(password)
        
        // then
        assertNotNull(result)
        assertFalse(result.value.isEmpty())
        // Argon2 해시는 $argon2로 시작
        assertTrue(result.value.startsWith("\$argon2"))
    }

    @Test
    @DisplayName("hashPassword - 동일한 비밀번호도 매번 다른 해시 생성 (salt 때문)")
    fun `hashPassword - 동일한 비밀번호도 매번 다른 해시 생성 (salt 때문)`() {
        // given
        val password = "samePassword"
        
        // when
        val hash1 = crypto.hashPassword(password)
        val hash2 = crypto.hashPassword(password)
        
        // then
        assertNotEquals(hash1, hash2) // salt가 매번 달라서 해시도 다름
    }

    @Test
    @DisplayName("isPasswordMatches - 올바른 비밀번호는 true 반환")
    fun `isPasswordMatches - 올바른 비밀번호는 true 반환`() {
        // given
        val password = "correctPassword"
        val hashedPassword = crypto.hashPassword(password)
        
        // when
        val result = crypto.isPasswordMatches(hashedPassword.value, password)
        
        // then
        assertTrue(result)
    }

    @Test
    @DisplayName("isPasswordMatches - 잘못된 비밀번호는 false 반환")
    fun `isPasswordMatches - 잘못된 비밀번호는 false 반환`() {
        // given
        val password = "correctPassword"
        val wrongPassword = "wrongPassword"
        val hashedPassword = crypto.hashPassword(password)
        
        // when
        val result = crypto.isPasswordMatches(hashedPassword.value, wrongPassword)
        
        // then
        assertFalse(result)
    }

    @Test
    @DisplayName("isPasswordMatches - 대소문자 구분 검증")
    fun `isPasswordMatches - 대소문자 구분 검증`() {
        // given
        val password = "Password"
        val hashedPassword = crypto.hashPassword(password)
        
        // when
        val correctResult = crypto.isPasswordMatches(hashedPassword.value, "Password")
        val wrongResult = crypto.isPasswordMatches(hashedPassword.value, "password")
        
        // then
        assertTrue(correctResult)
        assertFalse(wrongResult)
    }

    @Test
    @DisplayName("isPasswordMatches - 특수문자 포함 비밀번호 검증")
    fun `isPasswordMatches - 특수문자 포함 비밀번호 검증`() {
        // given
        val password = "P@ssw0rd!#$%^&*()"
        val hashedPassword = crypto.hashPassword(password)
        
        // when
        val result = crypto.isPasswordMatches(hashedPassword.value, password)
        
        // then
        assertTrue(result)
    }

    @Test
    @DisplayName("isPasswordMatches - 빈 비밀번호 검증")
    fun `isPasswordMatches - 빈 비밀번호 검증`() {
        // given
        val password = ""
        val hashedPassword = crypto.hashPassword(password)
        
        // when
        val result = crypto.isPasswordMatches(hashedPassword.value, password)
        
        // then
        assertTrue(result)
    }

    @Test
    @DisplayName("isPasswordMatches - 긴 비밀번호 검증")
    fun `isPasswordMatches - 긴 비밀번호 검증`() {
        // given
        val password = "a".repeat(100) // 100자 비밀번호
        val hashedPassword = crypto.hashPassword(password)
        
        // when
        val correctResult = crypto.isPasswordMatches(hashedPassword.value, password)
        val wrongResult = crypto.isPasswordMatches(hashedPassword.value, "a".repeat(99))
        
        // then
        assertTrue(correctResult)
        assertFalse(wrongResult)
    }

    @Test
    @DisplayName("isPasswordMatches - 유니코드 문자 포함 비밀번호 검증")
    fun `isPasswordMatches - 유니코드 문자 포함 비밀번호 검증`() {
        // given
        val password = "비밀번호123!@#"
        val hashedPassword = crypto.hashPassword(password)
        
        // when
        val result = crypto.isPasswordMatches(hashedPassword.value, password)
        
        // then
        assertTrue(result)
    }

}
