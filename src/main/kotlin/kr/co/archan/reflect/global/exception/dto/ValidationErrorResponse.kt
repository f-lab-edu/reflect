package kr.co.archan.reflect.global.exception.dto

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 유효성 검사 실패에 대한 확장된 ApiErrorResponseSpec 구현체
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ValidationErrorResponse(
    override val title: String,
    override val status: Int,
    override val detail: String? = null,
    override val instance: String? = null,
    val invalidFields: List<InvalidFieldDetail>
) : ApiErrorResponseSpec

/**
 * 유효성 검사 실패한 개별 필드 정보
 */
data class InvalidFieldDetail(
    val field: String,
    val userMessage: String
)
