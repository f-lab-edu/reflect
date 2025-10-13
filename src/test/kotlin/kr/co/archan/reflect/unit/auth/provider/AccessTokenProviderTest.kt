package kr.co.archan.reflect.unit.auth.provider

import com.nimbusds.jwt.SignedJWT
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import java.time.Instant
import kotlin.math.abs

class AccessTokenProviderTest : BehaviorSpec({
    
    fun createTestComponents() = object {
        val jwtProperties = mockk<JwtProperties> {
            every { issuer } returns "test-issuer"
            every { audience } returns "test-audience"
            every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough"
            every { accessTokenTtlSeconds } returns 3600L
        }
        val accessTokenProvider = AccessTokenProvider(jwtProperties)
    }

    context("provideAccessToken - JWT 토큰 생성 기본 기능") {
        Given("회원 정보가 주어지고") {
            val tc = createTestComponents()
            
            val memberId = 12345L
            val email = "test@example.com"
            val beforeCall = Instant.now()

            When("AccessToken을 생성하면") {
                val result = tc.accessTokenProvider.provideAccessToken(memberId, email)
                val afterCall = Instant.now()

                Then("유효한 JWT 토큰이 생성된다") {
                    result shouldNotBe null
                    result.value.isNotEmpty() shouldBe true
                    result.value.isNotBlank() shouldBe true
                    result.expiresAt.isAfter(beforeCall) shouldBe true
                    result.expiresAt.isAfter(afterCall) shouldBe true

                    // 만료시간 검증 (2초 이내 오차 허용)
                    val expectedExpiry = beforeCall.plusSeconds(3600L)
                    val timeDifference = abs(result.expiresAt.epochSecond - expectedExpiry.epochSecond)
                    timeDifference shouldBeLessThanOrEqualTo 2

                    // JWT 클레임 검증
                    val signedJWT = SignedJWT.parse(result.value)
                    val claims = signedJWT.jwtClaimsSet
                    claims.subject shouldBe memberId.toString()
                    claims.getStringClaim("email") shouldBe email
                    claims.getStringClaim("member_id") shouldBe memberId.toString()
                    claims.issuer shouldBe "test-issuer"
                    claims.audience shouldBe listOf("test-audience")
                }
            }
        }
    }

    context("provideAccessToken - TTL 오버플로우로 토큰 발급 실패") {
        Given("TTL이 Long.MAX_VALUE로 설정되고") {
            // 이 테스트는 특별한 설정이 필요하므로 별도로 provider 생성
            val overflowJwtProperties = mockk<JwtProperties> {
                every { issuer } returns "test-issuer"
                every { audience } returns "test-audience"
                every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough"
                every { accessTokenTtlSeconds } returns Long.MAX_VALUE
            }
            val overflowAccessTokenProvider = AccessTokenProvider(overflowJwtProperties)
            val memberId = 12345L
            val email = "test@example.com"

            When("AccessToken을 생성하려고 시도하면") {
                Then("ArithmeticException이 발생한다") {
                    shouldThrow<ArithmeticException> {
                        overflowAccessTokenProvider.provideAccessToken(memberId, email)
                    }
                }
            }
        }
    }
})
