package kr.co.archan.reflect.global.exception.handler

import jakarta.servlet.http.HttpServletRequest
import kr.co.archan.reflect.global.exception.common.InvalidInputException
import kr.co.archan.reflect.global.exception.dto.ApiErrorResponseSpec
import kr.co.archan.reflect.global.exception.dto.InvalidFieldDetail
import kr.co.archan.reflect.global.exception.dto.ValidationErrorResponse
import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import kr.co.archan.reflect.global.exception.types.InvalidInputErrorSpec
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
class ServiceExceptionHandler {

    /**
     * InvalidInputException 처리
     * RFC 9457 Problem Details 표준에 따른 응답 반환
     */
    @ExceptionHandler(InvalidInputException::class)
    fun handleInvalidInputException(
        ex: InvalidInputException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponseSpec> {

        // InvalidInputErrorSpec들을 InvalidFieldDetail로 변환
        val invalidFields = ex.specs.map { spec ->
            InvalidFieldDetail(
                field = spec.invalidField,
                userMessage = spec.userMessage
            )
        }
        
        // InvalidInputException은 항상 400 Bad Request로 응답
        val httpStatus = ex.httpStatus
        
        val problemDetail = ValidationErrorResponse(
            title = "Validation Failed",
            status = httpStatus.value(),
            detail = "요청 데이터의 유효성 검사에 실패했습니다.",
            instance = request.requestURI,
            invalidFields = invalidFields
        )
        
        return ResponseEntity(problemDetail, httpStatus)
    }
}