package kr.co.archan.reflect.global.exception.common

import kr.co.archan.reflect.global.exception.base.ApiException
import kr.co.archan.reflect.global.exception.types.InvalidInputErrorSpec
import org.springframework.http.HttpStatus

class InvalidInputException(val specs: List<InvalidInputErrorSpec>) : ApiException(specs) {
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}