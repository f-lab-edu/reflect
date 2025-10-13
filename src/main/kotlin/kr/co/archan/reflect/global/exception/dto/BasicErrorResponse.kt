package kr.co.archan.reflect.global.exception.dto

import jakarta.servlet.http.HttpServletRequest
import kr.co.archan.reflect.global.exception.base.ApiException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException

class BasicErrorResponse (
    override val title: String,
    override val status: Int,
    override val detail: String?,
    override val instance: String?
) : ApiErrorResponseSpec {
    constructor(ex: ApiException, request: HttpServletRequest) : this(
        title = ex.apiErrorSpec.systemMessage,
        status = ex.apiErrorSpec.httpStatus.value(),
        detail = ex.apiErrorSpec.userMessage,
        instance = request.requestURI
    )

    constructor(ex: AuthenticationException, request: HttpServletRequest) : this(
        title = parseAuthenticationErrorTitle(ex),
        status = 401,
        detail = parseAuthenticationErrorDetail(ex),
        instance = request.requestURI
    )

    constructor(ex: AccessDeniedException, request: HttpServletRequest) : this(
        title = "Permission_Denied",
        status = 403,
        detail = "접근 권한이 존재하지 않습니다.",
        instance = request.requestURI
    )
    
    companion object {
        private fun parseAuthenticationErrorTitle(ex: AuthenticationException): String {
            val raw = (ex.cause?.message ?: ex.message).orEmpty()
            return when {
                raw.contains("expired", true) -> "Token_Expired"
                raw.contains("Invalid issuer", true) || raw.contains("issuer", true) || raw.contains(" iss", true) -> "Invalid_Issuer"
                raw.contains("Invalid audience", true) || raw.contains("audience", true) -> "Invalid_Audience"
                raw.contains("signature", true) || raw.contains("JWS", true) || raw.contains("MAC", true) -> "Invalid_Signature"
                raw.contains("algorithm", true) -> "Invalid_Algorithm"
                else -> "Invalid_Token"
            }
        }
        
        private fun parseAuthenticationErrorDetail(ex: AuthenticationException): String {
            val raw = (ex.cause?.message ?: ex.message).orEmpty()
            return when {
                raw.contains("expired", true) -> "토큰이 만료되었습니다."
                raw.contains("Invalid issuer", true) || raw.contains("issuer", true) || raw.contains(" iss", true) -> "토큰 발급자가 올바르지 않습니다."
                raw.contains("Invalid audience", true) || raw.contains("audience", true) -> "토큰 대상(audience)이 올바르지 않습니다."
                raw.contains("signature", true) || raw.contains("JWS", true) || raw.contains("MAC", true) -> "토큰 서명이 유효하지 않습니다."
                raw.contains("algorithm", true) -> "허용되지 않은 서명 알고리즘입니다."
                else -> "유효하지 않은 토큰입니다."
            }
        }
    }
}