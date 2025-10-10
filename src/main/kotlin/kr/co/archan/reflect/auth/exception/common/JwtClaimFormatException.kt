package kr.co.archan.reflect.auth.exception.common

import kr.co.archan.reflect.auth.exception.types.JwtAuthErrorSpec

class JwtClaimFormatException : JwtAuthException(
    JwtAuthErrorSpec.CLAIM_FORMAT_INVALID
)
