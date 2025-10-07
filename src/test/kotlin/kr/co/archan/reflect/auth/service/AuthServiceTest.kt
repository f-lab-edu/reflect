package kr.co.archan.reflect.auth.service

import com.nimbusds.jwt.SignedJWT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.archan.reflect.auth.exception.common.WrongPasswordException
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.exception.common.MemberNotFoundException
import kr.co.archan.reflect.member.repository.MemberRepository
import kr.co.archan.reflect.member.service.MemberService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest {

    private lateinit var authService: AuthService
    private lateinit var memberService: MemberService
    private lateinit var tokenService: TokenService
    private lateinit var crypto: Crypto
    
    // Mock 필요한 외부 의존성
    private lateinit var memberRepository: MemberRepository
    private lateinit var jwtProperties: JwtProperties
    private lateinit var refreshTokenRepository: RefreshTokenRepository
    private lateinit var cryptoProperties: CryptoProperties
    private lateinit var passwordEncoder: PasswordEncoder

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
        
        // 실제 PasswordEncoder 사용
        passwordEncoder = Argon2PasswordEncoder(16, 32, 1, 64 * 1024, 3)

        crypto = Crypto(cryptoProperties, passwordEncoder)
        
        val accessTokenProvider = AccessTokenProvider(jwtProperties)
        val refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)
        
        tokenService = TokenService(accessTokenProvider, refreshTokenProvider)
        memberService = MemberService(memberRepository)
        authService = AuthService(memberService, tokenService, crypto)
    }

    @Test
    @DisplayName("loginMember - 올바른 이메일과 비밀번호로 로그인 성공")
    fun `loginMember - 올바른 이메일과 비밀번호로 로그인 성공`() {
        // given
        val email = "user@example.com"
        val rawPassword = "myPassword123!"
        val hashedPassword = crypto.hashPassword(rawPassword)
        
        val member = Member.signUp(email, hashedPassword, "홍길동")
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

        // when
        val result = authService.loginMember(email, rawPassword)

        // then
        assertNotNull(result)
        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
        
        // JWT 검증
        val jwt = SignedJWT.parse(result.accessToken.value)
        assertEquals(email, jwt.jwtClaimsSet.getStringClaim("email"))
        assertEquals(member.id.toString(), jwt.jwtClaimsSet.subject)
        
        // Repository 호출 검증
        verify(exactly = 1) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
        verify(exactly = 1) { refreshTokenRepository.save(any()) }
    }

    @Test
    @DisplayName("loginMember - 존재하지 않는 이메일로 로그인 시 MemberNotFoundException 발생")
    fun `loginMember - 존재하지 않는 이메일로 로그인 시 MemberNotFoundException 발생`() {
        // given
        val email = "notfound@example.com"
        val password = "anyPassword"
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

        // when & then
        assertThrows<MemberNotFoundException> {
            authService.loginMember(email, password)
        }
        
        verify(exactly = 1) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
    }

    @Test
    @DisplayName("loginMember - 잘못된 비밀번호로 로그인 시 WrongPasswordException 발생")
    fun `loginMember - 잘못된 비밀번호로 로그인 시 WrongPasswordException 발생`() {
        // given
        val email = "user@example.com"
        val correctPassword = "correctPassword123!"
        val wrongPassword = "wrongPassword123!"
        val hashedPassword = crypto.hashPassword(correctPassword)
        
        val member = Member.signUp(email, hashedPassword, "홍길동")
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

        // when & then
        assertThrows<WrongPasswordException> {
            authService.loginMember(email, wrongPassword)
        }
        
        verify(exactly = 1) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
    }

    @Test
    @DisplayName("loginMember - 같은 회원이 여러 번 로그인 가능")
    fun `loginMember - 같은 회원이 여러 번 로그인 가능`() {
        // given
        val email = "repeat@example.com"
        val password = "password123!"
        val hashedPassword = crypto.hashPassword(password)
        
        val member = Member.signUp(email, hashedPassword, "반복로그인")
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

        // when
        val result1 = authService.loginMember(email, password)
        val result2 = authService.loginMember(email, password)
        val result3 = authService.loginMember(email, password)

        // then
        // 매번 다른 토큰 발급
        assertNotEquals(result1.accessToken.value, result2.accessToken.value)
        assertNotEquals(result2.accessToken.value, result3.accessToken.value)
        assertNotEquals(result1.refreshToken.value, result2.refreshToken.value)
        
        verify(exactly = 3) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
        verify(exactly = 3) { refreshTokenRepository.save(any()) }
    }

    @Test
    @DisplayName("loginMember - 대소문자가 다른 비밀번호는 실패")
    fun `loginMember - 대소문자가 다른 비밀번호는 실패`() {
        // given
        val email = "case@example.com"
        val correctPassword = "Password123!"
        val wrongPassword = "password123!"  // 대소문자 다름
        val hashedPassword = crypto.hashPassword(correctPassword)
        
        val member = Member.signUp(email, hashedPassword, "대소문자")
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

        // when & then
        assertThrows<WrongPasswordException> {
            authService.loginMember(email, wrongPassword)
        }
    }

    @Test
    @DisplayName("loginMember - 특수문자가 포함된 비밀번호 검증")
    fun `loginMember - 특수문자가 포함된 비밀번호 검증`() {
        // given
        val email = "special@example.com"
        val password = "P@ssw0rd!#$%^&*()"
        val hashedPassword = crypto.hashPassword(password)
        
        val member = Member.signUp(email, hashedPassword, "특수문자")
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns member

        // when
        val result = authService.loginMember(email, password)

        // then
        assertNotNull(result)
        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
    }

    @Test
    @DisplayName("loginMember - 탈퇴한 회원은 로그인 불가")
    fun `loginMember - 탈퇴한 회원은 로그인 불가`() {
        // given
        val email = "withdrawn@example.com"
        val password = "password123!"
        // 탈퇴한 회원이므로 repository에서 null 반환
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

        // when & then
        assertThrows<MemberNotFoundException> {
            authService.loginMember(email, password)
        }
        
        verify(exactly = 1) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
    }
}