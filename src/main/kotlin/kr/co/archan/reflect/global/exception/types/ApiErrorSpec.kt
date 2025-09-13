package kr.co.archan.reflect.global.exception.types

interface ApiErrorSpec : ProductErrorSpec {
    val userMessage: String
}