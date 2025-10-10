package kr.co.archan.reflect.unit.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.co.archan.reflect.auth.controller.AuthController
import kr.co.archan.reflect.auth.dto.request.LoginRequest
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import kr.co.archan.reflect.auth.service.AuthService
import kr.co.archan.reflect.auth.service.TokenService
import kr.co.archan.reflect.global.exception.handler.ServiceExceptionHandler
import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.repository.MemberRepository
import kr.co.archan.reflect.member.service.MemberService
import org.springframework.http.MediaType
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest : BehaviorSpec({
    
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
        val authService = AuthService(memberService, tokenService, crypto)
        val authController = AuthController(authService)
        val mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(ServiceExceptionHandler())
            .build()
        val objectMapper = ObjectMapper()
    }

    context("POST /auth/login - 올바른 이메일과 비밀번호로 로그인 성공") {
        Given("올바른 이메일과 비밀번호를 가진 회원이 있고") {
            val tc = createTestComponents()
            
            val email = "user@example.com"
            val rawPassword = "myPassword123!"
            val hashedPassword = tc.crypto.hashPassword(rawPassword)
            val member = kr.co.archan.reflect.member.domain.Member.signUp(email, hashedPassword, "홍길동")
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member
            val request = LoginRequest(email, rawPassword)

            When("로그인을 요청하면") {
                Then("로그인이 성공하고 토큰이 반환된다") {
                    tc.mockMvc.perform(
                        post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tc.objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().isOk)
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.accessToken").exists())
                        .andExpect(jsonPath("$.refreshToken").exists())
                        .andExpect(jsonPath("$.accessToken").isNotEmpty)
                        .andExpect(jsonPath("$.refreshToken").isNotEmpty)
                }
            }
        }
    }

    context("POST /auth/login - 존재하지 않는 이메일로 로그인 시 실패") {
        Given("존재하지 않는 이메일로 요청하고") {
            val tc = createTestComponents()
            
            val email = "notfound@example.com"
            val password = "anyPassword"
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null
            val request = LoginRequest(email, password)

            When("로그인을 요청하면") {
                Then("로그인이 실패하고 4xx 에러가 반환된다") {
                    tc.mockMvc.perform(
                        post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tc.objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().is4xxClientError)
                }
            }
        }
    }

    context("POST /auth/login - 잘못된 비밀번호로 로그인 시 실패") {
        Given("잘못된 비밀번호로 요청하고") {
            val tc = createTestComponents()
            
            val email = "user@example.com"
            val correctPassword = "correctPassword123!"
            val wrongPassword = "wrongPassword123!"
            val hashedPassword = tc.crypto.hashPassword(correctPassword)
            val member = kr.co.archan.reflect.member.domain.Member.signUp(email, hashedPassword, "홍길동")
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member
            val request = LoginRequest(email, wrongPassword)

            When("로그인을 요청하면") {
                Then("로그인이 실패하고 4xx 에러가 반환된다") {
                    tc.mockMvc.perform(
                        post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tc.objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().is4xxClientError)
                }
            }
        }
    }

    context("POST /auth/login - 빈 이메일로 요청 시 검증 실패") {
        Given("빈 이메일로 요청하고") {
            val tc = createTestComponents()
            
            val request = LoginRequest("", "password123!")

            When("로그인을 요청하면") {
                Then("검증이 실패하고 4xx 에러가 반환된다") {
                    tc.mockMvc.perform(
                        post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tc.objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().is4xxClientError)
                }
            }
        }
    }

    context("POST /auth/login - 빈 비밀번호로 요청 시 검증 실패") {
        Given("빈 비밀번호로 요청하고") {
            val tc = createTestComponents()
            
            val request = LoginRequest("user@example.com", "")

            When("로그인을 요청하면") {
                Then("검증이 실패하고 4xx 에러가 반환된다") {
                    tc.mockMvc.perform(
                        post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tc.objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().is4xxClientError)
                }
            }
        }
    }

    context("POST /auth/login - 잘못된 JSON 형식으로 요청 시 실패") {
        Given("잘못된 JSON 형식으로 요청하고") {
            val tc = createTestComponents()

            When("로그인을 요청하면") {
                Then("JSON 파싱이 실패하고 4xx 에러가 반환된다") {
                    tc.mockMvc.perform(
                        post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"invalid\" \"json\"}")
                    )
                        .andExpect(status().is4xxClientError)
                }
            }
        }
    }
})
