package kr.co.archan.reflect.unit.member.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import kr.co.archan.reflect.auth.service.TokenService
import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.domain.Member
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

class MemberTest : BehaviorSpec({

    fun createTestComponents() = object {
        private val testPepperKey = "test-pepper-key"
        val cryptoProperties = CryptoProperties(
            hashKey = "test-secret-key",
            pepperKey = testPepperKey
        )
        // 실제 Argon2PasswordEncoder 인스턴스 사용
        val passwordEncoder = Argon2PasswordEncoder(16, 32, 1, 64 * 1024, 3)
        val crypto = Crypto(cryptoProperties, passwordEncoder)
    }

    context("signUp - 성공") {
        Given("유효한 이메일, 패스워드, 이름이 주어지고") {
            val tc = createTestComponents()
            val email = "test@example.com"
            val password = "securePassword123!"
            val name = "홍길동"

            When("Member를 생성하면") {
                val member = Member.signUp(email, tc.crypto.hashPassword(password), name)

                Then("Member가 올바르게 생성된다") {
                    member.id shouldBe 0
                    member.email shouldBe email
                    tc.crypto.isPasswordMatches(member.password, password) shouldBe true
                    member.name shouldBe name
                }
            }
        }
    }
})
