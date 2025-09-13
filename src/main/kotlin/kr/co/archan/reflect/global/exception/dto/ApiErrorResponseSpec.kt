package kr.co.archan.reflect.global.exception.dto

/**
 * RFC 9457 Problem Details for HTTP APIs 표준에 따른 에러 응답 인터페이스
 */
interface ApiErrorResponseSpec {
    val title: String

    val status: Int

    val detail: String?

    val instance: String?
}
