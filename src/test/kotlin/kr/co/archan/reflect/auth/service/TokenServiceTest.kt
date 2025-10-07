package kr.co.archan.reflect.auth.service

import com.nimbusds.jwt.SignedJWT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.archan.reflect.auth.properties.JwtProperties
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.auth.repository.RefreshTokenRepository
import kr.co.archan.reflect.member.domain.Member
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.math.abs

class TokenServiceTest {

    private lateinit var tokenService: TokenService
    private lateinit var accessTokenProvider: AccessTokenProvider
    private lateinit var refreshTokenProvider: RefreshTokenProvider
    private lateinit var jwtProperties: JwtProperties
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @BeforeEach
    fun setUp() {
        // Properties 모킹 (설정값)
        jwtProperties = mockk {
            every { issuer } returns "test-issuer"
            every { audience } returns "test-audience"
            every { secret } returns "test-secret-key-for-jwt-signing-must-be-long-enough-at-least-256-bits"
            every { accessTokenTtlSeconds } returns 3600L
            every { refreshTokenTtlSeconds } returns 1209600L
        }
        
        // Repository 모킹
        refreshTokenRepository = mockk(relaxed = true)
        
        // 실제 Provider 인스턴스 생성
        accessTokenProvider = AccessTokenProvider(jwtProperties)
        refreshTokenProvider = RefreshTokenProvider(jwtProperties, refreshTokenRepository)

        // Service 인스턴스 생성
        tokenService = TokenService(accessTokenProvider, refreshTokenProvider)
    }

    @Test
    @DisplayName("issueOnLogin - 로그인 시 AccessToken과 RefreshToken 발급")
    fun `issueOnLogin - 로그인 시 AccessToken과 RefreshToken 발급`() {
        // given
        val member = Member.signUp(
            email = "test@example.com",
            password = "hashedPassword",
            name = "홍길동"
        )
        val beforeCall = Instant.now()

        // when
        val result = tokenService.issueOnLogin(member)
        val afterCall = Instant.now()

        // then
        assertNotNull(result)
        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
        
        // AccessToken 검증
        assertNotNull(result.accessToken.value)
        assertFalse(result.accessToken.value.isEmpty())
        assertTrue(result.accessToken.expiresAt.isAfter(beforeCall))
        
        // AccessToken JWT 파싱 및 검증
        val jwt = SignedJWT.parse(result.accessToken.value)
        val claims = jwt.jwtClaimsSet
        assertEquals(member.id.toString(), claims.subject)
        assertEquals(member.email, claims.getStringClaim("email"))
        assertEquals("test-issuer", claims.issuer)
        assertEquals("test-audience", claims.audience[0])
        
        // RefreshToken 검증
        assertNotNull(result.refreshToken.value)
        assertFalse(result.refreshToken.value.isEmpty())
        assertEquals(member.id.toString(), result.refreshToken.memberId)
        assertTrue(result.refreshToken.expiresAt.isAfter(afterCall))
        
        // 만료 시간이 대략적으로 올바른지 검증 (오차 허용)
        val expectedAccessExpiry = beforeCall.plusSeconds(3600L)
        val accessTimeDiff = abs(result.accessToken.expiresAt.epochSecond - expectedAccessExpiry.epochSecond)
        assertTrue(accessTimeDiff <= 2)
        
        val expectedRefreshExpiry = beforeCall.plusSeconds(1209600L)
        val refreshTimeDiff = abs(result.refreshToken.expiresAt.epochSecond - expectedRefreshExpiry.epochSecond)
        assertTrue(refreshTimeDiff <= 2)
        
        // Repository에 저장하는 동작을 호출하였는지 검증
        verify(exactly = 1) { refreshTokenRepository.save(any()) }
    }

    @Test
    @DisplayName("issueOnLogin - 서로 다른 이메일을 가진 회원에게 다른 토큰 발급")
    fun `issueOnLogin - 서로 다른 이메일을 가진 회원에게 다른 토큰 발급`() {
        // given
        val member1 = Member.signUp("user1@example.com", "password1", "사용자1")
        val member2 = Member.signUp("user2@example.com", "password2", "사용자2")

        // when
        val result1 = tokenService.issueOnLogin(member1)
        val result2 = tokenService.issueOnLogin(member2)

        // then
        // AccessToken이 서로 다름 (JWT 내용이 다르므로)
        assertNotEquals(result1.accessToken.value, result2.accessToken.value)
        
        // RefreshToken이 서로 다름 (랜덤 생성되므로)
        assertNotEquals(result1.refreshToken.value, result2.refreshToken.value)
        
        // 각 JWT에 올바른 이메일이 포함되어 있는지 검증
        val jwt1 = SignedJWT.parse(result1.accessToken.value)
        val jwt2 = SignedJWT.parse(result2.accessToken.value)
        assertEquals("user1@example.com", jwt1.jwtClaimsSet.getStringClaim("email"))
        assertEquals("user2@example.com", jwt2.jwtClaimsSet.getStringClaim("email"))
        
        // Repository에 저장하는 동작이 2번 저장되었는지 검증
        verify(exactly = 2) { refreshTokenRepository.save(any()) }
    }

    @Test
    @DisplayName("issueOnLogin - JWT에 올바른 claim이 포함됨")
    fun `issueOnLogin - JWT에 올바른 claim이 포함됨`() {
        // given
        val member = Member.signUp(
            email = "verify@example.com",
            password = "password",
            name = "검증테스트"
        )

        // when
        val result = tokenService.issueOnLogin(member)

        // then
        val jwt = SignedJWT.parse(result.accessToken.value)
        val claims = jwt.jwtClaimsSet
        
        // 필수 claim 검증
        assertEquals(member.id.toString(), claims.subject)
        assertEquals(member.id.toString(), claims.getStringClaim("member_id"))
        assertEquals(member.email, claims.getStringClaim("email"))
        assertEquals("test-issuer", claims.issuer)
        assertEquals(listOf("test-audience"), claims.audience)
        assertNotNull(claims.jwtid)
        assertNotNull(claims.issueTime)
        assertNotNull(claims.expirationTime)
    }

    @Test
    @DisplayName("issueOnLogin - RefreshToken이 Repository에 저장됨")
    fun `issueOnLogin - RefreshToken이 Repository에 저장됨`() {
        // given
        val member = Member.signUp("test@example.com", "password", "테스트")

        // when
        val result = tokenService.issueOnLogin(member)

        // then
        assertNotNull(result.refreshToken)
        verify(exactly = 1) { 
            refreshTokenRepository.save(match { token ->
                token.value == result.refreshToken.value &&
                token.memberId == member.id.toString() &&
                token.expiresAt == result.refreshToken.expiresAt
            })
        }
    }

    @Test
    @DisplayName("issueOnLogin - 같은 회원이 여러 번 로그인하면 매번 다른 토큰 발급")
    fun `issueOnLogin - 같은 회원이 여러 번 로그인하면 매번 다른 토큰 발급`() {
        // given
        val member = Member.signUp("repeat@example.com", "password", "반복테스트")

        // when
        val result1 = tokenService.issueOnLogin(member)
        val result2 = tokenService.issueOnLogin(member)
        val result3 = tokenService.issueOnLogin(member)

        // then
        // 모든 토큰이 서로 다름
        assertNotEquals(result1.accessToken.value, result2.accessToken.value)
        assertNotEquals(result2.accessToken.value, result3.accessToken.value)
        assertNotEquals(result1.accessToken.value, result3.accessToken.value)
        
        assertNotEquals(result1.refreshToken.value, result2.refreshToken.value)
        assertNotEquals(result2.refreshToken.value, result3.refreshToken.value)
        assertNotEquals(result1.refreshToken.value, result3.refreshToken.value)
        
        // Repository에 3번 저장됨
        verify(exactly = 3) { refreshTokenRepository.save(any()) }
    }

    @Test
    @DisplayName("issueOnLogin - 토큰 만료 시간이 올바르게 설정됨")
    fun `issueOnLogin - 토큰 만료 시간이 Properties 설정에 따라 올바르게 설정됨`() {
        // given
        val member = Member.signUp("expiry@example.com", "password", "만료테스트")
        val now = Instant.now()

        // when
        val result = tokenService.issueOnLogin(member)

        // then
        // AccessToken은 약 1시간 후 만료 (3600초)
        val expectedAccessExpiry = now.plusSeconds(3600L)
        val accessTimeDiff = abs(result.accessToken.expiresAt.epochSecond - expectedAccessExpiry.epochSecond)
        assertTrue(accessTimeDiff <= 2, "AccessToken expiry time should be around 1 hour from now")
        
        // RefreshToken은 약 14일 후 만료 (1209600초)
        val expectedRefreshExpiry = now.plusSeconds(1209600L)
        val refreshTimeDiff = abs(result.refreshToken.expiresAt.epochSecond - expectedRefreshExpiry.epochSecond)
        assertTrue(refreshTimeDiff <= 2, "RefreshToken expiry time should be around 14 days from now")
        
        // RefreshToken이 AccessToken보다 훨씬 나중에 만료됨
        assertTrue(result.refreshToken.expiresAt.isAfter(result.accessToken.expiresAt))
    }
}