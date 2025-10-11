package kr.co.archan.reflect.member.exception.types

import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import org.springframework.http.HttpStatus

enum class MemberErrorCode(
    override val systemMessage: String,
    override val userMessage: String,
    override val httpStatus: HttpStatus
) : ApiErrorSpec {
    
    MEMBER_NOT_FOUND(
        systemMessage = "Member not found",
        userMessage = "존재하지 않는 회원입니다.",
        httpStatus = HttpStatus.BAD_REQUEST
    )
}

