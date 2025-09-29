package kr.co.archan.reflect.auth.exception.common

import kr.co.archan.reflect.auth.exception.types.JwtAuthErrorSpec

class JwtConversionException : JwtAuthException(
    JwtAuthErrorSpec.CONVERSION_FAILED
)
