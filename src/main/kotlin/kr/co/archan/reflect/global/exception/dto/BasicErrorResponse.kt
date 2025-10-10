package kr.co.archan.reflect.global.exception.dto

class BasicErrorResponse(
    override val title: String,
    override val status: Int,
    override val detail: String?,
    override val instance: String?
) : ApiErrorResponseSpec