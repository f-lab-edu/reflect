package kr.co.archan.reflect.global.exception.handler

import jakarta.servlet.http.HttpServletRequest
import kr.co.archan.reflect.global.exception.base.ApiException
import kr.co.archan.reflect.global.exception.dto.ApiErrorResponseSpec
import kr.co.archan.reflect.global.exception.dto.BasicErrorResponse
import kr.co.archan.reflect.global.exception.dto.InvalidFieldDetail
import kr.co.archan.reflect.global.exception.dto.ValidationErrorResponse
import kr.co.archan.reflect.member.exception.types.MemberInvalidInputField
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ServiceExceptionHandler {

    /**
     * ApiException 처리
     * RFC 9457 Problem Details 표준에 따른 응답 반환
     */
    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        ex: ApiException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponseSpec> {
        val problemDetail = BasicErrorResponse(ex, request)
        return ResponseEntity(problemDetail, ex.apiErrorSpec.httpStatus)
    }

    /**
     * MethodArgumentNotValidException 처리
     * RFC 9457 Problem Details 표준에 따른 응답 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponseSpec> {
        val invalidFields = ex.bindingResult.fieldErrors.map { fe ->
            val spec = when (fe.code) {
                "ValidEmail" -> MemberInvalidInputField.EMAIL
                "ValidName" -> MemberInvalidInputField.NAME
                "ValidPassword" -> MemberInvalidInputField.PASSWORD
                else -> MemberInvalidInputField.UNKNOWN
            }
            InvalidFieldDetail(
                field = spec.invalidField,
                userMessage = spec.userMessage
            )
        }

        val problemDetail = ValidationErrorResponse(
            title = "Validation Failed",
            status = HttpStatus.BAD_REQUEST.value(),
            detail = "요청 데이터의 유효성 검사에 실패했습니다.",
            instance = request.requestURI,
            invalidFields = invalidFields
        )

        return ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST)
    }
}