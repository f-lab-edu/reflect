package kr.co.archan.reflect.global.util

import kr.co.archan.reflect.global.properties.CryptoProperties
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class Crypto(
    private val cryptoProperties: CryptoProperties,
    private val passwordEncoder: PasswordEncoder
) {
    fun sha256WithNoSalt(s: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(cryptoProperties.hashKey.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(s.toByteArray()))
    }

    fun hashPassword(password: String): String {
        return passwordEncoder.encode(password + cryptoProperties.pepperKey)
    }

    fun isPasswordMatches(storedHash: String, inputPassword: String): Boolean {
        val material = inputPassword + cryptoProperties.pepperKey
        return passwordEncoder.matches(material, storedHash)
    }

}