package kr.co.archan.reflect.global.exception.base

import kr.co.archan.reflect.global.exception.types.ApiErrorSpec

abstract class ApiException(val apiErrorSpec: ApiErrorSpec) : ProductException(apiErrorSpec)