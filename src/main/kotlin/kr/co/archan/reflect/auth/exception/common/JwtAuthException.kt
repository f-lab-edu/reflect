package kr.co.archan.reflect.auth.exception.common

import kr.co.archan.reflect.auth.exception.types.JwtAuthErrorSpec
import kr.co.archan.reflect.global.exception.base.ApiException
import org.springframework.http.HttpStatus

open class JwtAuthException(spec: JwtAuthErrorSpec) : ApiException(spec) {
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED
}
