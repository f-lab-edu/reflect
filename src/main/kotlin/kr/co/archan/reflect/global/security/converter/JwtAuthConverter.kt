package kr.co.archan.reflect.global.security.converter

import kr.co.archan.reflect.auth.domain.MemberPrincipal
import kr.co.archan.reflect.auth.exception.common.AuthException
import kr.co.archan.reflect.auth.exception.types.AuthErrorCode
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter : Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        try {
            // 필수 클레임 검증
            val memberIdStr = jwt.getClaimAsString("member_id")
                ?: throw AuthException(AuthErrorCode.JWT_REQUIRED_CLAIM_MISSING)
            
            val email = jwt.getClaimAsString("email")
                ?: throw AuthException(AuthErrorCode.JWT_REQUIRED_CLAIM_MISSING)
            
            // memberId 타입 변환
            val memberId = try {
                memberIdStr.toLong()
            } catch (e: NumberFormatException) {
                throw AuthException(AuthErrorCode.JWT_CLAIM_FORMAT_INVALID)
            }
            
            val principal = MemberPrincipal(memberId = memberId, email = email)
            
            val roles = (jwt.claims["roles"] as? Collection<*>)?.map { it.toString() } ?: listOf("USER")
            val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
            
            return UsernamePasswordAuthenticationToken(principal, "N/A", authorities)
            
        } catch (e: AuthException) {
            throw e
        } catch (e: Exception) {
            throw AuthException(AuthErrorCode.JWT_CONVERSION_FAILED)
        }
    }
}
