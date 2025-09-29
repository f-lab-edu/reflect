package kr.co.archan.reflect.auth.exception.common

import kr.co.archan.reflect.auth.exception.types.JwtAuthErrorSpec

class JwtRequiredClaimMissingException : JwtAuthException(
    JwtAuthErrorSpec.REQUIRED_CLAIM_MISSING
)
