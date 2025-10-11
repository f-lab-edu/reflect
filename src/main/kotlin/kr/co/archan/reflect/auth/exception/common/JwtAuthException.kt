package kr.co.archan.reflect.auth.exception.common

import kr.co.archan.reflect.auth.exception.types.JwtAuthErrorSpec
import kr.co.archan.reflect.global.exception.base.ApiException

open class JwtAuthException(spec: JwtAuthErrorSpec) : ApiException(spec)
