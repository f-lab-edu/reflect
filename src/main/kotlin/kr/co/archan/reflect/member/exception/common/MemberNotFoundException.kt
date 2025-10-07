package kr.co.archan.reflect.member.exception.common

import kr.co.archan.reflect.global.exception.base.ApiException
import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import org.springframework.http.HttpStatus

class MemberNotFoundException : ApiException(spec) {
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST

    companion object {
        private val spec = object : ApiErrorSpec {
            override val userMessage: String = "존재하지 않는 회원입니다."
            override val systemMessage: String = "Member not found"
        }
    }
}