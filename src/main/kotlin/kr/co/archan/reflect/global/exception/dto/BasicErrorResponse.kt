package kr.co.archan.reflect.global.exception.dto

import jakarta.servlet.http.HttpServletRequest
import kr.co.archan.reflect.global.exception.base.ApiException

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
}