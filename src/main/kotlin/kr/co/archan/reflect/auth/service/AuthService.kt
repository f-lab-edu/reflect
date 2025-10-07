package kr.co.archan.reflect.auth.service

import kr.co.archan.reflect.auth.domain.AccessToken
import kr.co.archan.reflect.auth.domain.RefreshToken
import kr.co.archan.reflect.auth.dto.response.LoginResponse
import kr.co.archan.reflect.auth.dto.vo.AuthToken
import kr.co.archan.reflect.auth.exception.common.WrongPasswordException
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.service.MemberService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService (
    private val memberService: MemberService,
    private val tokenService: TokenService,
    private val crypto: Crypto
){

    @Transactional
    fun loginMember(email: String, password: String) : AuthToken {
        val member = memberService.getMember(email)
        if(!crypto.isPasswordMatches(member.password, password)) throw WrongPasswordException()
        return tokenService.issueOnLogin(member)
    }

}