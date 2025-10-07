package kr.co.archan.reflect.auth.exception.common

import kr.co.archan.reflect.global.exception.base.ApiException
import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import org.springframework.http.HttpStatus

class WrongPasswordException : ApiException(spec) {
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED

    companion object {
        private val spec = object : ApiErrorSpec {
            override val userMessage: String = "비밀번호가 일치하지 않습니다"
            override val systemMessage: String = "Wrong password"
        }
    }
}