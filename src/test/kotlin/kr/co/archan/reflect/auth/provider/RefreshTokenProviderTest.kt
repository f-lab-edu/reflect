package kr.co.archan.reflect.auth.provider

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kr.co.archan.reflect.auth.domain.RefreshToken
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import java.time.Instant
import kotlin.math.abs

class RefreshTokenProviderTest : BehaviorSpec({
    
    fun createTestComponents() = object {
        val jwtProperties = mockk<JwtProperties> {
            every { refreshTokenTtlSeconds } returns 604800L // 7일
        }
        val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
    }

    context("provideRefreshToken - 리프레시 토큰 생성 기본 기능") {
        Given("회원 ID가 주어지고") {
            val tc = createTestComponents()
            
            val tokenSlot = slot<RefreshToken>()
            every { tc.refreshTokenRepository.save(capture(tokenSlot)) } returns mockk()
            val memberId = 12345L
            val beforeCall = Instant.now()

            When("RefreshToken을 생성하면") {
                val result = tc.refreshTokenProvider.provideRefreshToken(memberId)
                val afterCall = Instant.now()

                Then("유효한 RefreshToken이 생성되고 저장된다") {
                    result shouldNotBe null
                    result.value.isNotEmpty() shouldBe true
                    result.value.isNotBlank() shouldBe true
                    result.memberId shouldBe memberId.toString()
                    result.expiresAt.isAfter(beforeCall) shouldBe true
                    result.expiresAt.isAfter(afterCall) shouldBe true

                    // 만료시간 검증 (2초 이내 오차 허용)
                    val expectedExpiry = beforeCall.plusSeconds(604800L)
                    val timeDifference = abs(result.expiresAt.epochSecond - expectedExpiry.epochSecond)
                    timeDifference shouldBeLessThanOrEqualTo 2

                    // repository.save 호출 검증
                    verify(exactly = 1) { tc.refreshTokenRepository.save(any()) }

                    // 저장된 토큰 검증
                    val savedToken = tokenSlot.captured
                    savedToken.value shouldBe result.value
                    savedToken.memberId shouldBe result.memberId
                    savedToken.expiresAt shouldBe result.expiresAt
                }
            }
        }
    }

    context("provideRefreshToken - 랜덤 토큰 생성 검증") {
        Given("회원 ID가 주어지고") {
            val tc = createTestComponents()
            
            val memberId = 12345L

            When("동일한 memberId로 여러 번 토큰을 생성하면") {
                val token1 = tc.refreshTokenProvider.provideRefreshToken(memberId)
                val token2 = tc.refreshTokenProvider.provideRefreshToken(memberId)

                Then("매번 다른 랜덤 토큰이 생성된다") {
                    token1.value shouldNotBe token2.value
                    token1.value.length shouldBe 43
                    token2.value.length shouldBe 43

                    // Base64 URL 인코딩 형식 검증 (패딩 없음)
                    token1.value.contains('=') shouldBe false
                    token2.value.contains('=') shouldBe false
                    token1.value.matches(Regex("^[A-Za-z0-9_-]+$")) shouldBe true
                    token2.value.matches(Regex("^[A-Za-z0-9_-]+$")) shouldBe true
                }
            }
        }
    }

    context("provideRefreshToken - 다른 회원에게 다른 토큰 발급") {
        Given("서로 다른 회원 ID가 주어지고") {
            val tc = createTestComponents()
            
            val memberId1 = 100L
            val memberId2 = 200L

            When("각각 토큰을 생성하면") {
                val token1 = tc.refreshTokenProvider.provideRefreshToken(memberId1)
                val token2 = tc.refreshTokenProvider.provideRefreshToken(memberId2)

                Then("memberId가 다르고 토큰도 다르다") {
                    token1.memberId shouldBe memberId1.toString()
                    token2.memberId shouldBe memberId2.toString()
                    token1.value shouldNotBe token2.value
                }
            }
        }
    }

    context("provideRefreshToken - TTL 오버플로우로 토큰 발급 실패") {
        Given("TTL이 Long.MAX_VALUE로 설정되고") {
            val tc = createTestComponents()
            
            // 이 테스트는 특별한 설정이 필요하므로 별도로 provider 생성
            val overflowJwtProperties = mockk<JwtProperties> {
                every { refreshTokenTtlSeconds } returns Long.MAX_VALUE
            }
            val overflowRefreshTokenProvider = RefreshTokenProvider(overflowJwtProperties, tc.refreshTokenRepository)
            val memberId = 12345L

            When("RefreshToken을 생성하려고 시도하면") {
                Then("ArithmeticException이 발생한다") {
                    shouldThrow<ArithmeticException> {
                        overflowRefreshTokenProvider.provideRefreshToken(memberId)
                    }
                }
            }
        }
    }
})
