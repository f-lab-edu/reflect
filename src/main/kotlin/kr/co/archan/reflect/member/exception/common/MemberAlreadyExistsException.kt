package kr.co.archan.reflect.member.exception.common

import kr.co.archan.reflect.global.exception.base.ApiException
import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import org.springframework.http.HttpStatus

class MemberAlreadyExistsException : ApiException(spec) {
    override val httpStatus: HttpStatus = HttpStatus.CONFLICT

    companion object {
        private val spec = object : ApiErrorSpec {
            override val userMessage: String = "이미 존재하는 회원입니다."
            override val systemMessage: String = "Member already exists"
        }
    }
}