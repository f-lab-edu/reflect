package kr.co.archan.reflect.auth.service

import kr.co.archan.reflect.auth.dto.vo.AuthToken
import kr.co.archan.reflect.auth.provider.AccessTokenProvider
import kr.co.archan.reflect.auth.provider.RefreshTokenProvider
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TokenService(
    private val accessTokenProvider: AccessTokenProvider,
    private val refreshTokenProvider: RefreshTokenProvider,
) {

    /**
     * 토큰 발급을 시도하는 메서드.
     *
     * 이 메서드는 다음 단계를 수행한다:
     * 1. 외부 Provider로부터 Access Token과 Refresh Token을 발급받는다.
     * 2. Refresh Token을 Redis에 저장한다.
     *
     * 전파 수준(Propagation level)은 MANDATORY로 설정되어 있으며, 다른 메서드 내부에서만 호출되도록 설계되었다.
     *
     * @return AccessToken과 RefreshToken의 쌍
     */
    @Transactional(propagation = Propagation.MANDATORY)
    fun issueToken(member: Member): AuthToken {
        val accessToken = accessTokenProvider.provideAccessToken(member.id, member.email)
        val refreshToken = refreshTokenProvider.provideRefreshToken(member.id)

        return AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

}