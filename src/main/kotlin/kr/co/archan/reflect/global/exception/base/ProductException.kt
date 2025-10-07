package kr.co.archan.reflect.global.exception.base

import kr.co.archan.reflect.global.exception.types.ProductErrorSpec

abstract class ProductException(spec: ProductErrorSpec) : RuntimeException(spec.systemMessage)