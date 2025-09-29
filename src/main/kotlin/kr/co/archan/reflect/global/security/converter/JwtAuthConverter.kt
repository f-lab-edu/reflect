package kr.co.archan.reflect.global.security.converter

import kr.co.archan.reflect.auth.domain.MemberPrincipal
import kr.co.archan.reflect.auth.exception.common.JwtAuthException
import kr.co.archan.reflect.auth.exception.common.JwtClaimFormatException
import kr.co.archan.reflect.auth.exception.common.JwtConversionException
import kr.co.archan.reflect.auth.exception.common.JwtRequiredClaimMissingException
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
                ?: throw JwtRequiredClaimMissingException()
            
            val email = jwt.getClaimAsString("email")
                ?: throw JwtRequiredClaimMissingException()
            
            // memberId 타입 변환
            val memberId = try {
                memberIdStr.toLong()
            } catch (e: NumberFormatException) {
                throw JwtClaimFormatException()
            }
            
            val principal = MemberPrincipal(memberId = memberId, email = email)
            
            val roles = (jwt.claims["roles"] as? Collection<*>)?.map { it.toString() } ?: listOf("USER")
            val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
            
            return UsernamePasswordAuthenticationToken(principal, "N/A", authorities)
            
        } catch (e: JwtAuthException) {
            throw e
        } catch (e: Exception) {
            throw JwtConversionException()
        }
    }
}
