package kr.co.archan.reflect.global.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class Crypto(
    @Value("\${security.sha.secret}")
    private val secretKey: String
) {
    fun sha256WithNoSalt(s: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(s.toByteArray()))
    }
}