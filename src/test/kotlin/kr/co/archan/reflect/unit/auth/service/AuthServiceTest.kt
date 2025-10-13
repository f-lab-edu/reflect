package kr.co.archan.reflect.unit.auth.service

import com.nimbusds.jwt.SignedJWT
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.archan.reflect.auth.exception.common.AuthException
import kr.co.archan.reflect.auth.exception.types.AuthErrorCode
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import kr.co.archan.reflect.auth.service.AuthService
import kr.co.archan.reflect.auth.service.TokenService
import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.global.util.DistributedLockManager
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.exception.common.MemberException
import kr.co.archan.reflect.member.exception.types.MemberErrorCode
import kr.co.archan.reflect.member.repository.MemberRepository
import kr.co.archan.reflect.member.service.MemberService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

class AuthServiceTest : BehaviorSpec({
    
    fun createTestComponents() = object {
        val jwtProperties = mockk<JwtProperties> {
            every { issuer } returns "test-issuer"
            every { audience } returns "test-audience"
            every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough-at-least-256-bits"
            every { accessTokenTtlSeconds } returns 3600L
            every { refreshTokenTtlSeconds } returns 1209600L
        }
        val cryptoProperties = CryptoProperties(
            hashKey = "test-hash-key",
            pepperKey = "test-pepper"
        )
        val memberRepository = mockk<MemberRepository>()
        val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
        val passwordEncoder = Argon2PasswordEncoder(16, 32, 1, 64 * 1024, 3)
        val crypto = Crypto(cryptoProperties, passwordEncoder)
        val accessTokenProvider = AccessTokenProvider(jwtProperties)
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        val tokenService = TokenService(accessTokenProvider, refreshTokenProvider)
        val memberService = MemberService(memberRepository)
        val distributedLockManager = mockk<DistributedLockManager>(relaxed = true)
        val authService = AuthService(memberService, tokenService, crypto, distributedLockManager)
    }

    context("loginMember - 올바른 이메일과 비밀번호로 로그인 성공") {
        Given("올바른 이메일과 비밀번호를 가진 회원이 있고") {
            val tc = createTestComponents()
            
            val email = "user@example.com"
            val rawPassword = "myPassword123!"
            val hashedPassword = tc.crypto.hashPassword(rawPassword)
            val member = Member.signUp(email, hashedPassword, "홍길동")
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

            When("로그인을 시도하면") {
                val result = tc.authService.loginMember(email, rawPassword)

                Then("로그인이 성공하고 토큰이 발급된다") {
                    result shouldNotBe null
                    result.accessToken shouldNotBe null
                    result.refreshToken shouldNotBe null
                    
                    // JWT 검증
                    val jwt = SignedJWT.parse(result.accessToken.value)
                    jwt.jwtClaimsSet.getStringClaim("email") shouldBe email
                    jwt.jwtClaimsSet.subject shouldBe member.id.toString()
                    
                    // Repository 호출 검증
                    verify(exactly = 1) { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) }
                    verify(exactly = 1) { tc.refreshTokenRepository.save(any()) }
                }
            }
        }
    }

    context("loginMember - 존재하지 않는 이메일로 로그인 시 MemberNotFoundException 발생") {
        Given("존재하지 않는 이메일로 로그인을 시도하고") {
            val tc = createTestComponents()
            
            val email = "notfound@example.com"
            val password = "anyPassword"
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

            When("로그인을 시도하면") {
                Then("MemberNotFoundException이 발생한다") {
                    val exception = shouldThrow<MemberException> {
                        tc.authService.loginMember(email, password)
                    }
                    
                    // ErrorCode 검증
                    exception.apiErrorSpec shouldBe MemberErrorCode.MEMBER_NOT_FOUND
                    
                    verify(exactly = 1) { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) }
                }
            }
        }
    }

    context("loginMember - 잘못된 비밀번호로 로그인 시 WrongPasswordException 발생") {
        Given("잘못된 비밀번호로 로그인을 시도하고") {
            val tc = createTestComponents()
            
            val email = "user@example.com"
            val correctPassword = "correctPassword123!"
            val wrongPassword = "wrongPassword123!"
            val hashedPassword = tc.crypto.hashPassword(correctPassword)
            val member = Member.signUp(email, hashedPassword, "홍길동")
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

            When("로그인을 시도하면") {
                Then("WrongPasswordException이 발생한다") {
                    val exception = shouldThrow<AuthException> {
                        tc.authService.loginMember(email, wrongPassword)
                    }
                    
                    // ErrorCode 검증
                    exception.apiErrorSpec shouldBe AuthErrorCode.WRONG_PASSWORD
                    
                    verify(exactly = 1) { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) }
                }
            }
        }
    }

    context("loginMember - 같은 회원이 여러 번 로그인 가능") {
        Given("같은 회원이 있고") {
            val tc = createTestComponents()
            
            val email = "repeat@example.com"
            val password = "password123!"
            val hashedPassword = tc.crypto.hashPassword(password)
            val member = Member.signUp(email, hashedPassword, "반복로그인")
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

            When("여러 번 로그인을 시도하면") {
                val result1 = tc.authService.loginMember(email, password)
                val result2 = tc.authService.loginMember(email, password)
                val result3 = tc.authService.loginMember(email, password)

                Then("매번 다른 토큰이 발급된다") {
                    // 매번 다른 토큰 발급
                    result1.accessToken.value shouldNotBe result2.accessToken.value
                    result2.accessToken.value shouldNotBe result3.accessToken.value
                    result1.refreshToken.value shouldNotBe result2.refreshToken.value
                    
                    verify(exactly = 3) { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) }
                    verify(exactly = 3) { tc.refreshTokenRepository.save(any()) }
                }
            }
        }
    }

    context("loginMember - 대소문자가 다른 비밀번호는 실패") {
        Given("대소문자가 다른 비밀번호로 로그인을 시도하고") {
            val tc = createTestComponents()
            
            val email = "case@example.com"
            val correctPassword = "Password123!"
            val wrongPassword = "password123!"  // 대소문자 다름
            val hashedPassword = tc.crypto.hashPassword(correctPassword)
            val member = Member.signUp(email, hashedPassword, "대소문자")
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

            When("로그인을 시도하면") {
                Then("WrongPasswordException이 발생한다") {
                    val exception = shouldThrow<AuthException> {
                        tc.authService.loginMember(email, wrongPassword)
                    }
                    
                    // ErrorCode 검증
                    exception.apiErrorSpec shouldBe AuthErrorCode.WRONG_PASSWORD
                }
            }
        }
    }

    context("loginMember - 특수문자가 포함된 비밀번호 검증") {
        Given("특수문자가 포함된 비밀번호를 가진 회원이 있고") {
            val tc = createTestComponents()
            
            val email = "special@example.com"
            val password = "P@ssw0rd!#$%^&*()"
            val hashedPassword = tc.crypto.hashPassword(password)
            val member = Member.signUp(email, hashedPassword, "특수문자")
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

            When("로그인을 시도하면") {
                val result = tc.authService.loginMember(email, password)

                Then("로그인이 성공한다") {
                    result shouldNotBe null
                    result.accessToken shouldNotBe null
                    result.refreshToken shouldNotBe null
                }
            }
        }
    }

    context("loginMember - 탈퇴한 회원은 로그인 불가") {
        Given("탈퇴한 회원이 있고") {
            val tc = createTestComponents()
            
            val email = "withdrawn@example.com"
            val password = "password123!"
            // 탈퇴한 회원이므로 repository에서 null 반환
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

            When("로그인을 시도하면") {
                Then("MemberNotFoundException이 발생한다") {
                    val exception = shouldThrow<MemberException> {
                        tc.authService.loginMember(email, password)
                    }
                    
                    // ErrorCode 검증
                    exception.apiErrorSpec shouldBe MemberErrorCode.MEMBER_NOT_FOUND
                    
                    verify(exactly = 1) { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) }
                }
            }
        }
    }
})
