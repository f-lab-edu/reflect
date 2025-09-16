package kr.co.archan.reflect.global.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(
    @Value("\${security.jwt.secret}") private val secret: String,
    @Value("\${security.jwt.issuer}") private val issuer: String,
) {

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")

        val decoder = NimbusJwtDecoder
            .withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS256)  // alg 고정
            .build()

        // 기본(exp/nbf) + issuer 검증
        val validator = JwtValidators.createDefaultWithIssuer(issuer)

        decoder.setJwtValidator(DelegatingOAuth2TokenValidator(validator))
        return decoder
    }
}