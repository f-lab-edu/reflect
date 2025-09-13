package kr.co.archan.reflect.global.exception.base

import kr.co.archan.reflect.global.exception.types.ProductErrorSpec

abstract class ProductException : RuntimeException {
    private val specs: List<ProductErrorSpec>

    // 단일 스펙
    constructor(spec: ProductErrorSpec) : super(spec.systemMessage) {
        this.specs = listOf(spec)
    }

    // 여러 스펙
    constructor(specs: List<ProductErrorSpec>) : super(
        specs.joinToString(";") { it.systemMessage }
    ) {
        require(specs.isNotEmpty()) { "At least one ProductErrorSpec is required" }
        this.specs = specs
    }

}