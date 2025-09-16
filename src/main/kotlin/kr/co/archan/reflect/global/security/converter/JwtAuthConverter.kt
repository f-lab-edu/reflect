package kr.co.archan.reflect.global.security.converter

import kr.co.archan.reflect.auth.domain.MemberPrincipal
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter : Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val principal = MemberPrincipal(
            memberId = jwt.getClaimAsString("member_id").toLong(),
            email = jwt.getClaimAsString("email")
        )
        val roles = (jwt.claims["roles"] as? Collection<*>)?.map { it.toString() } ?: listOf("USER")
        val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
        return UsernamePasswordAuthenticationToken(principal, "N/A", authorities)
    }
}
