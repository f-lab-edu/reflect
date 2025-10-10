package kr.co.archan.reflect.auth.provider

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import kr.co.archan.reflect.auth.domain.AccessToken
import kr.co.archan.reflect.auth.properties.JwtProperties
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class AccessTokenProvider (
    private val properties: JwtProperties
){
    private val signer = MACSigner(properties.secret.toByteArray(Charsets.UTF_8))

    fun provideAccessToken(memberId: Long, email: String): AccessToken {
        val now = Instant.now()
        val exp = now.plusSeconds(properties.accessTokenTtlSeconds)

        val header = JWSHeader.Builder(JWSAlgorithm.HS256)
            .type(JOSEObjectType.JWT)
            .build()

        val claims = JWTClaimsSet.Builder()
            .subject(memberId.toString())
            .issuer(properties.issuer)
            .audience(properties.audience)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(exp))
            .jwtID(UUID.randomUUID().toString())
            .claim("member_id", memberId.toString())
            .claim("email", email)
            .build()

        val jwt = SignedJWT(header, claims).apply { sign(signer) }.serialize()
        return AccessToken(value = jwt, expiresAt = exp)
    }
}