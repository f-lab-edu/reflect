package kr.co.archan.reflect.unit.global.security.converter

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.collections.shouldContain
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import kr.co.archan.reflect.auth.domain.MemberPrincipal
import kr.co.archan.reflect.auth.exception.common.AuthException
import kr.co.archan.reflect.auth.exception.types.AuthErrorCode
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JwtAuthConverterTest : BehaviorSpec({
    
    fun createTestComponents() = object {
        val jwtAuthConverter = JwtAuthConverter()
    }

    context("convert - 기본 권한 (roles 없음)") {
        Given("roles 클레임이 없는 JWT가 주어지고") {
            val tc = createTestComponents()
            
            val jwt = mockk<Jwt> {
                every { getClaimAsString("member_id") } returns "12345"
                every { getClaimAsString("email") } returns "test@example.com"
                every { claims } returns mapOf(
                    "member_id" to "12345",
                    "email" to "test@example.com"
                )
            }

            When("JWT를 변환하면") {
                val result = tc.jwtAuthConverter.convert(jwt)

                Then("UsernamePasswordAuthenticationToken이 반환되고 기본 권한이 설정된다") {
                    result shouldNotBe null
                    result.shouldBeInstanceOf<UsernamePasswordAuthenticationToken>()
                    
                    // Principal 검증
                    val principal = result.principal as MemberPrincipal
                    principal.memberId shouldBe 12345L
                    principal.email shouldBe "test@example.com"
                    
                    // 기본 권한 검증
                    result.authorities.size shouldBe 1
                    result.authorities shouldContain SimpleGrantedAuthority("ROLE_USER")
                    
                    // Credentials 검증
                    result.credentials shouldBe "N/A"
                }
            }
        }
    }

    context("convert - 커스텀 권한 (roles 있음)") {
        Given("roles 클레임이 있는 JWT가 주어지고") {
            val tc = createTestComponents()
            
            val jwt = mockk<Jwt> {
                every { getClaimAsString("member_id") } returns "67890"
                every { getClaimAsString("email") } returns "admin@example.com"
                every { claims } returns mapOf(
                    "member_id" to "67890",
                    "email" to "admin@example.com",
                    "roles" to listOf("ADMIN", "USER")
                )
            }

            When("JWT를 변환하면") {
                val result = tc.jwtAuthConverter.convert(jwt)

                Then("UsernamePasswordAuthenticationToken이 반환되고 커스텀 권한이 설정된다") {
                    result shouldNotBe null
                    
                    // Principal 검증
                    val principal = result.principal as MemberPrincipal
                    principal.memberId shouldBe 67890L
                    principal.email shouldBe "admin@example.com"
                    
                    // 커스텀 권한 검증
                    result.authorities.size shouldBe 2
                    result.authorities shouldContain SimpleGrantedAuthority("ROLE_ADMIN")
                    result.authorities shouldContain SimpleGrantedAuthority("ROLE_USER")
                }
            }
        }
    }

    context("convert - member_id 클레임 누락시 실패") {
        Given("member_id 클레임이 없는 JWT가 주어지고") {
            val tc = createTestComponents()
            
            val jwt = mockk<Jwt> {
                every { getClaimAsString("member_id") } returns null
                every { getClaimAsString("email") } returns "test@example.com"
                every { claims } returns mapOf("email" to "test@example.com")
            }

            When("JWT를 변환하면") {
                Then("AuthException이 발생한다") {
                    val exception = shouldThrow<AuthException> {
                        tc.jwtAuthConverter.convert(jwt)
                    }
                    
                    // ErrorCode 검증
                    exception.apiErrorSpec shouldBe AuthErrorCode.JWT_REQUIRED_CLAIM_MISSING
                }
            }
        }
    }

    context("convert - email 클레임 누락시 실패") {
        Given("email 클레임이 없는 JWT가 주어지고") {
            val tc = createTestComponents()
            
            val jwt = mockk<Jwt> {
                every { getClaimAsString("member_id") } returns "12345"
                every { getClaimAsString("email") } returns null
                every { claims } returns mapOf("member_id" to "12345")
            }

            When("JWT를 변환하면") {
                Then("AuthException이 발생한다") {
                    val exception = shouldThrow<AuthException> {
                        tc.jwtAuthConverter.convert(jwt)
                    }
                    
                    // ErrorCode 검증
                    exception.apiErrorSpec shouldBe AuthErrorCode.JWT_REQUIRED_CLAIM_MISSING
                }
            }
        }
    }

    context("convert - member_id 타입 변환 실패시 실패") {
        Given("member_id가 숫자가 아닌 JWT가 주어지고") {
            val tc = createTestComponents()
            
            val jwt = mockk<Jwt> {
                every { getClaimAsString("member_id") } returns "invalid_number"
                every { getClaimAsString("email") } returns "test@example.com"
                every { claims } returns mapOf(
                    "member_id" to "invalid_number",
                    "email" to "test@example.com"
                )
            }

            When("JWT를 변환하면") {
                Then("AuthException이 발생한다") {
                    val exception = shouldThrow<AuthException> {
                        tc.jwtAuthConverter.convert(jwt)
                    }
                    
                    // ErrorCode 검증
                    exception.apiErrorSpec shouldBe AuthErrorCode.JWT_CLAIM_FORMAT_INVALID
                }
            }
        }
    }

    context("convert - 다른 입력값으로 다른 Principal 생성") {
        Given("서로 다른 정보를 가진 두 개의 JWT가 주어지고") {
            val tc = createTestComponents()
            
            val jwt1 = mockk<Jwt> {
                every { getClaimAsString("member_id") } returns "11111"
                every { getClaimAsString("email") } returns "user1@example.com"
                every { claims } returns mapOf(
                    "member_id" to "11111",
                    "email" to "user1@example.com"
                )
            }
            
            val jwt2 = mockk<Jwt> {
                every { getClaimAsString("member_id") } returns "22222"
                every { getClaimAsString("email") } returns "user2@example.com"
                every { claims } returns mapOf(
                    "member_id" to "22222",
                    "email" to "user2@example.com"
                )
            }

            When("각각 JWT를 변환하면") {
                val result1 = tc.jwtAuthConverter.convert(jwt1)
                val result2 = tc.jwtAuthConverter.convert(jwt2)

                Then("서로 다른 Principal이 생성된다") {
                    val principal1 = result1.principal as MemberPrincipal
                    val principal2 = result2.principal as MemberPrincipal
                    
                    // 각각 다른 Principal 생성 검증
                    principal1.memberId shouldNotBe principal2.memberId
                    principal1.email shouldNotBe principal2.email
                    
                    // 각각의 값 정확성 검증
                    principal1.memberId shouldBe 11111L
                    principal1.email shouldBe "user1@example.com"
                    principal2.memberId shouldBe 22222L
                    principal2.email shouldBe "user2@example.com"
                    
                    // 둘 다 기본 권한 가져야 함
                    result1.authorities.size shouldBe 1
                    result2.authorities.size shouldBe 1
                    result1.authorities shouldContain SimpleGrantedAuthority("ROLE_USER")
                    result2.authorities shouldContain SimpleGrantedAuthority("ROLE_USER")
                }
            }
        }
    }
})
