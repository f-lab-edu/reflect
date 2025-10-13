package kr.co.archan.reflect.global.exception.types

import org.springframework.http.HttpStatus

interface ApiErrorSpec : ProductErrorSpec {
    val userMessage: String
    val httpStatus: HttpStatus
}