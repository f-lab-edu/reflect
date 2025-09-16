package kr.co.archan.reflect.global.util

import java.security.MessageDigest
import java.util.*

object Crypto {
     fun sha256(s: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return Base64.getUrlEncoder().withoutPadding().encodeToString(md.digest(s.toByteArray()))
    }
}