package kr.co.archan.reflect.global.exception.handler

import kr.co.archan.reflect.global.exception.common.InvalidInputException
import kr.co.archan.reflect.member.exception.types.MemberInvalidInputField
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class SystemExceptionToServiceExceptionHandler {

    /**
     * @RequestBody DTO(@Valid) 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): Nothing {
        val specs = ex.bindingResult.fieldErrors.map { fe ->
            when (fe.code) {
                "ValidEmail" -> MemberInvalidInputField.EMAIL
                "ValidName" -> MemberInvalidInputField.NAME
                "ValidPassword" -> MemberInvalidInputField.PASSWORD
                else -> MemberInvalidInputField.UNKNOWN
            }
        }
        throw InvalidInputException(specs)
    }

}