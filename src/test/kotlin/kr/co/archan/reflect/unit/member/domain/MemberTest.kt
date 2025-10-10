package kr.co.archan.reflect.unit.member.domain

import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.domain.Member
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


class MemberTest {

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
    @DisplayName("signUp 성공")
    fun `signUp 성공`() {
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "홍길동"
        val hashedPassword = crypto.hashPassword(password)


        val member = Member.signUp(email, hashedPassword, name)

        assertAll("Member 생성",
            { assertEquals(0, member.id) },
            { assertEquals(email, member.email) },
            { assertTrue(crypto.isPasswordMatches(hashedPassword.value, password)) },
            { assertEquals(name, member.name) }
        )
    }

}