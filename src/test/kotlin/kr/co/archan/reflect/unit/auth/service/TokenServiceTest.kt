package kr.co.archan.reflect.unit.auth.service

import com.nimbusds.jwt.SignedJWT
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import kr.co.archan.reflect.auth.service.TokenService
import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.domain.Member
import java.time.Instant
import kotlin.math.abs

class TokenServiceTest : BehaviorSpec({
    
    fun createTestComponents() = object {
        val jwtProperties = mockk<JwtProperties> {
            every { issuer } returns "test-issuer"
            every { audience } returns "test-audience"
            every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough-at-least-256-bits"
            every { accessTokenTtlSeconds } returns 3600L
            every { refreshTokenTtlSeconds } returns 1209600L
        }
        val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
        val accessTokenProvider = AccessTokenProvider(jwtProperties)
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        val tokenService = TokenService(accessTokenProvider, refreshTokenProvider)
    }

    context("issueOnLogin - 로그인 시 AccessToken과 RefreshToken 발급") {
        Given("회원 정보가 주어지고") {
            val tc = createTestComponents()
            
            val member = Member.signUp(
                email = "test@example.com",
                password = "hashedPassword",
                name = "홍길동"
            )
            val beforeCall = Instant.now()

            When("토큰을 발급하면") {
                val result = tc.tokenService.issueOnLogin(member)
                val afterCall = Instant.now()

                Then("AccessToken과 RefreshToken이 정상적으로 발급되고 검증된다") {
                    result shouldNotBe null
                    result.accessToken shouldNotBe null
                    result.refreshToken shouldNotBe null
                    
                    // AccessToken 검증
                    result.accessToken.value shouldNotBe null
                    result.accessToken.value.isEmpty() shouldBe false
                    result.accessToken.expiresAt.isAfter(beforeCall) shouldBe true
                    
                    // AccessToken JWT 파싱 및 검증
                    val jwt = SignedJWT.parse(result.accessToken.value)
                    val claims = jwt.jwtClaimsSet
                    claims.subject shouldBe member.id.toString()
                    claims.getStringClaim("email") shouldBe member.email
                    claims.issuer shouldBe "test-issuer"
                    claims.audience[0] shouldBe "test-audience"
                    
                    // RefreshToken 검증
                    result.refreshToken.value shouldNotBe null
                    result.refreshToken.value.isEmpty() shouldBe false
                    result.refreshToken.memberId shouldBe member.id.toString()
                    result.refreshToken.expiresAt.isAfter(afterCall) shouldBe true
                    
                    // 만료 시간이 대략적으로 올바른지 검증 (오차 허용)
                    val expectedAccessExpiry = beforeCall.plusSeconds(3600L)
                    val accessTimeDiff = abs(result.accessToken.expiresAt.epochSecond - expectedAccessExpiry.epochSecond)
                    accessTimeDiff.shouldBeLessThanOrEqualTo(2)
                    
                    val expectedRefreshExpiry = beforeCall.plusSeconds(1209600L)
                    val refreshTimeDiff = abs(result.refreshToken.expiresAt.epochSecond - expectedRefreshExpiry.epochSecond)
                    refreshTimeDiff.shouldBeLessThanOrEqualTo(2)
                    
                    // Repository에 저장하는 동작을 호출하였는지 검증
                    verify(exactly = 1) { tc.refreshTokenRepository.save(any()) }
                }
            }
        }
    }

    context("issueOnLogin - 서로 다른 이메일을 가진 회원에게 다른 토큰 발급") {
        Given("서로 다른 이메일을 가진 두 회원이 주어지고") {
            val tc = createTestComponents()
            
            val member1 = Member.signUp("user1@example.com", "password1", "사용자1")
            val member2 = Member.signUp("user2@example.com", "password2", "사용자2")

            When("각각 토큰을 발급하면") {
                val result1 = tc.tokenService.issueOnLogin(member1)
                val result2 = tc.tokenService.issueOnLogin(member2)

                Then("서로 다른 토큰이 발급되고 올바른 이메일이 포함된다") {
                    // AccessToken이 서로 다름 (JWT 내용이 다르므로)
                    result1.accessToken.value shouldNotBe result2.accessToken.value
                    
                    // RefreshToken이 서로 다름 (랜덤 생성되므로)
                    result1.refreshToken.value shouldNotBe result2.refreshToken.value
                    
                    // 각 JWT에 올바른 이메일이 포함되어 있는지 검증
                    val jwt1 = SignedJWT.parse(result1.accessToken.value)
                    val jwt2 = SignedJWT.parse(result2.accessToken.value)
                    jwt1.jwtClaimsSet.getStringClaim("email") shouldBe "user1@example.com"
                    jwt2.jwtClaimsSet.getStringClaim("email") shouldBe "user2@example.com"
                    
                    // Repository에 저장하는 동작이 2번 저장되었는지 검증
                    verify(exactly = 2) { tc.refreshTokenRepository.save(any()) }
                }
            }
        }
    }

    context("issueOnLogin - JWT에 올바른 claim이 포함됨") {
        Given("회원 정보가 주어지고") {
            val tc = createTestComponents()
            
            val member = Member.signUp(
                email = "verify@example.com",
                password = "password",
                name = "검증테스트"
            )

            When("토큰을 발급하면") {
                val result = tc.tokenService.issueOnLogin(member)

                Then("JWT에 필수 claim이 모두 포함된다") {
                    val jwt = SignedJWT.parse(result.accessToken.value)
                    val claims = jwt.jwtClaimsSet
                    
                    // 필수 claim 검증
                    claims.subject shouldBe member.id.toString()
                    claims.getStringClaim("member_id") shouldBe member.id.toString()
                    claims.getStringClaim("email") shouldBe member.email
                    claims.issuer shouldBe "test-issuer"
                    claims.audience shouldBe listOf("test-audience")
                    claims.jwtid shouldNotBe null
                    claims.issueTime shouldNotBe null
                    claims.expirationTime shouldNotBe null
                }
            }
        }
    }

    context("issueOnLogin - RefreshToken이 Repository에 저장됨") {
        Given("회원 정보가 주어지고") {
            val tc = createTestComponents()
            
            val member = Member.signUp("test@example.com", "password", "테스트")

            When("토큰을 발급하면") {
                val result = tc.tokenService.issueOnLogin(member)

                Then("RefreshToken이 Repository에 저장된다") {
                    result.refreshToken shouldNotBe null
                    verify(exactly = 1) { 
                        tc.refreshTokenRepository.save(match { token ->
                            token.value == result.refreshToken.value &&
                            token.memberId == member.id.toString() &&
                            token.expiresAt == result.refreshToken.expiresAt
                        })
                    }
                }
            }
        }
    }

    context("issueOnLogin - 같은 회원이 여러 번 로그인하면 매번 다른 토큰 발급") {
        Given("같은 회원이 주어지고") {
            val tc = createTestComponents()
            
            val member = Member.signUp("repeat@example.com", "password", "반복테스트")

            When("여러 번 토큰을 발급하면") {
                val result1 = tc.tokenService.issueOnLogin(member)
                val result2 = tc.tokenService.issueOnLogin(member)
                val result3 = tc.tokenService.issueOnLogin(member)

                Then("매번 다른 토큰이 발급된다") {
                    // 모든 토큰이 서로 다름
                    result1.accessToken.value shouldNotBe result2.accessToken.value
                    result2.accessToken.value shouldNotBe result3.accessToken.value
                    result1.accessToken.value shouldNotBe result3.accessToken.value
                    
                    result1.refreshToken.value shouldNotBe result2.refreshToken.value
                    result2.refreshToken.value shouldNotBe result3.refreshToken.value
                    result1.refreshToken.value shouldNotBe result3.refreshToken.value
                    
                    // Repository에 3번 저장됨
                    verify(exactly = 3) { tc.refreshTokenRepository.save(any()) }
                }
            }
        }
    }

    context("issueOnLogin - 토큰 만료 시간이 Properties 설정에 따라 올바르게 설정됨") {
        Given("회원 정보가 주어지고") {
            val tc = createTestComponents()
            
            val member = Member.signUp("expiry@example.com", "password", "만료테스트")
            val now = Instant.now()

            When("토큰을 발급하면") {
                val result = tc.tokenService.issueOnLogin(member)

                Then("토큰 만료 시간이 올바르게 설정된다") {
                    // AccessToken은 약 1시간 후 만료 (3600초)
                    val expectedAccessExpiry = now.plusSeconds(3600L)
                    val accessTimeDiff = abs(result.accessToken.expiresAt.epochSecond - expectedAccessExpiry.epochSecond)
                    accessTimeDiff.shouldBeLessThanOrEqualTo(2)
                    
                    // RefreshToken은 약 14일 후 만료 (1209600초)
                    val expectedRefreshExpiry = now.plusSeconds(1209600L)
                    val refreshTimeDiff = abs(result.refreshToken.expiresAt.epochSecond - expectedRefreshExpiry.epochSecond)
                    refreshTimeDiff.shouldBeLessThanOrEqualTo(2)
                    
                    // RefreshToken이 AccessToken보다 훨씬 나중에 만료됨
                    result.refreshToken.expiresAt.isAfter(result.accessToken.expiresAt) shouldBe true
                }
            }
        }
    }
})
