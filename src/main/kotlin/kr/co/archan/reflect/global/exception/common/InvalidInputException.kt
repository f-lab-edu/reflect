package kr.co.archan.reflect.global.exception.common

import kr.co.archan.reflect.global.exception.base.ProductException
import kr.co.archan.reflect.global.exception.types.InvalidInputField
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class InvalidInputException(val field: InvalidInputField) : ProductException("잘못된 값을 입력하였습니다")