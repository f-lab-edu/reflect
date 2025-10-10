package kr.co.archan.reflect.auth.service

import kr.co.archan.reflect.auth.dto.vo.AuthToken
import kr.co.archan.reflect.auth.exception.common.AuthException
import kr.co.archan.reflect.auth.exception.types.AuthErrorCode
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService (
    private val memberService: MemberService,
    private val tokenService: TokenService,
    private val crypto: Crypto
){

    @Transactional(readOnly = true)
    fun loginMember(email: String, password: String) : AuthToken {
        val member = memberService.getMember(email)
        if(!crypto.isPasswordMatches(member.password, password)) throw WrongPasswordException()
        return tokenService.issueToken(member)
    }

    @Transactional
    fun signUpMember(email: String, password: String, name: String): AuthToken {
        val hashedPassword = crypto.hashPassword(password)
        val member = Member.signUp(email, hashedPassword, name)
        val savedMember = memberService.saveNewMember(member)
        return tokenService.issueToken(savedMember)
    }

}