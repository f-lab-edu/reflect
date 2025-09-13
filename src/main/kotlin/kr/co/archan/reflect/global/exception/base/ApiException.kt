package kr.co.archan.reflect.global.exception.base

import kr.co.archan.reflect.global.exception.types.ApiErrorSpec
import org.springframework.http.HttpStatus

abstract class ApiException : ProductException {
    private val apiErrorSpecs: List<ApiErrorSpec>
    abstract val httpStatus: HttpStatus

    // 단일 API 에러 스펙
    constructor(spec: ApiErrorSpec) : super(spec) {
        this.apiErrorSpecs = listOf(spec)
    }

    // 여러 API 에러 스펙
    constructor(specs: List<ApiErrorSpec>) : super(specs) {
        this.apiErrorSpecs = specs
    }

}