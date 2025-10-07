package kr.co.archan.reflect.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
data class CryptoProperties(
    val hashKey: String,
    val pepperKey: String
)

