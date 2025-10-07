package kr.co.archan.reflect.global.exception.base

import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import org.springframework.http.HttpStatus

abstract class ApiException(val apiErrorSpec: ApiErrorSpec) : ProductException(apiErrorSpec) {
    abstract val httpStatus: HttpStatus
}