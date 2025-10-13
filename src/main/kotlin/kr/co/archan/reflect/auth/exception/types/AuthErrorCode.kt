package kr.co.archan.reflect.auth.exception.types

import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val systemMessage: String,
    override val userMessage: String,
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED
) : ApiErrorSpec {
    
    WRONG_PASSWORD(
        systemMessage = "Wrong password",
        userMessage = "비밀번호가 일치하지 않습니다"
    ),
    
    JWT_REQUIRED_CLAIM_MISSING(
        systemMessage = "JWT에 필수 클레임이 누락되었습니다",
        userMessage = "인증 토큰이 유효하지 않습니다"
    ),
    
    JWT_CLAIM_FORMAT_INVALID(
        systemMessage = "JWT 클레임의 형식이 올바르지 않습니다",
        userMessage = "인증 토큰이 유효하지 않습니다"
    ),
    
    JWT_CONVERSION_FAILED(
        systemMessage = "JWT를 Authentication으로 변환하는 중 오류가 발생했습니다",
        userMessage = "인증 처리 중 오류가 발생했습니다"
    )
}

