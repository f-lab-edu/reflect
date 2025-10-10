package kr.co.archan.reflect.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import kr.co.archan.reflect.auth.dto.request.LoginRequest
import kr.co.archan.reflect.auth.dto.request.SignUpRequest
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import kr.co.archan.reflect.auth.service.AuthService
import kr.co.archan.reflect.auth.service.TokenService
import kr.co.archan.reflect.global.exception.handler.ServiceExceptionHandler
import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.repository.MemberRepository
import kr.co.archan.reflect.member.service.MemberService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.MediaType
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var authController: AuthController
    private lateinit var authService: AuthService
    private lateinit var objectMapper: ObjectMapper
    
    // Mock 필요한 외부 의존성
    private lateinit var memberRepository: MemberRepository
    private lateinit var jwtProperties: JwtProperties
    private lateinit var refreshTokenRepository: RefreshTokenRepository
    private lateinit var cryptoProperties: CryptoProperties
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var crypto: Crypto

    @BeforeEach
    fun setUp() {
        // Properties 모킹
        jwtProperties = mockk {
            every { issuer } returns "test-issuer"
            every { audience } returns "test-audience"
            every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough-at-least-256-bits"
            every { accessTokenTtlSeconds } returns 3600L
            every { refreshTokenTtlSeconds } returns 1209600L
        }
        
        cryptoProperties = mockk {
            every { hashKey } returns "test-hash-key"
            every { pepperKey } returns "test-pepper"
        }
        
        // 외부 저장소 모킹
        memberRepository = mockk()
        refreshTokenRepository = mockk(relaxed = true)
        
        // 실제 인스턴스 생성
        passwordEncoder = Argon2PasswordEncoder(16, 32, 1, 64 * 1024, 3)
        crypto = Crypto(cryptoProperties, passwordEncoder)
        
        val accessTokenProvider = AccessTokenProvider(jwtProperties)
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        val tokenService = TokenService(accessTokenProvider, refreshTokenProvider)
        val memberService = MemberService(memberRepository)
        
        authService = AuthService(memberService, tokenService, crypto)
        authController = AuthController(authService)
        
        // MockMvc 설정 (standalone + ExceptionHandler)
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(ServiceExceptionHandler())
            .build()
        objectMapper = ObjectMapper()
    }

    @Test
    @DisplayName("POST /auth/login - 올바른 이메일과 비밀번호로 로그인 성공")
    fun `POST auth login - 올바른 이메일과 비밀번호로 로그인 성공`() {
        // given
        val email = "user@example.com"
        val rawPassword = "myPassword123!"
        val hashedPassword = crypto.hashPassword(rawPassword)
        
        val member = Member.signUp(email, hashedPassword, "홍길동")
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member
        
        val request = LoginRequest(email, rawPassword)

        // when & then
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.refreshToken").isNotEmpty)
    }

    @Test
    @DisplayName("POST /auth/login - 존재하지 않는 이메일로 로그인 시 실패")
    fun `POST auth login - 존재하지 않는 이메일로 로그인 시 실패`() {
        // given
        val email = "notfound@example.com"
        val password = "anyPassword"
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null
        
        val request = LoginRequest(email, password)

        // when & then
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("POST /auth/login - 잘못된 비밀번호로 로그인 시 실패")
    fun `POST auth login - 잘못된 비밀번호로 로그인 시 실패`() {
        // given
        val email = "user@example.com"
        val correctPassword = "correctPassword123!"
        val wrongPassword = "wrongPassword123!"
        val hashedPassword = crypto.hashPassword(correctPassword)
        
        val member = Member.signUp(email, hashedPassword, "홍길동")
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member
        
        val request = LoginRequest(email, wrongPassword)

        // when & then
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("POST /auth/login - 빈 이메일로 요청 시 검증 실패")
    fun `POST auth login - 빈 이메일로 요청 시 검증 실패`() {
        // given
        val request = LoginRequest("", "password123!")

        // when & then
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("POST /auth/login - 빈 비밀번호로 요청 시 검증 실패")
    fun `POST auth login - 빈 비밀번호로 요청 시 검증 실패`() {
        // given
        val request = LoginRequest("user@example.com", "")

        // when & then
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @DisplayName("POST /auth/signup - 올바른 정보로 회원가입 성공")
    fun `POST auth signup - 올바른 정보로 회원가입 성공`() {
        // given
        val email = "abc@gmail.com"
        val password = "abcdefg1!"
        val name = "abcd"

        val request = SignUpRequest(email, password, name)

        every { memberRepository.save(any()) } returns Member.signUp(email, crypto.hashPassword(password), name)
        every { memberRepository.existsByEmailAndIsWithdrawnFalse(any()) } returns false

        // when & then
        mockMvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.refreshToken").isNotEmpty)
    }

}