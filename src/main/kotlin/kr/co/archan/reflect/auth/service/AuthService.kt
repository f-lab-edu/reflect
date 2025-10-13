package kr.co.archan.reflect.auth.service

import kr.co.archan.reflect.auth.dto.vo.AuthToken
import kr.co.archan.reflect.auth.exception.common.AuthException
import kr.co.archan.reflect.auth.exception.types.AuthErrorCode
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.global.util.DistributedLockManager
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService (
    private val memberService: MemberService,
    private val tokenService: TokenService,
    private val crypto: Crypto,
    private val distributedLockManager: DistributedLockManager
){

    @Transactional(readOnly = true)
    fun loginMember(email: String, password: String) : AuthToken {
        val member = memberService.getMember(email)
        if(!crypto.isPasswordMatches(member.password, password)) throw AuthException(AuthErrorCode.WRONG_PASSWORD)
        return tokenService.issueToken(member)
    }

    @Transactional
    fun signUpMember(email: String, password: String, name: String): AuthToken {
        val hashedPassword = crypto.hashPassword(password)
        val member = Member.signUp(email, hashedPassword, name)
        
        // 락 획득 후 DB 저장
        val savedMember = distributedLockManager.executeWithLock(
            key = email,
            prefix = "auth:signup",
        ) {
            memberService.saveNewMember(member)
        }
        
        // 3. 락 해제 후 토큰 발급
        return tokenService.issueToken(savedMember)
    }

}