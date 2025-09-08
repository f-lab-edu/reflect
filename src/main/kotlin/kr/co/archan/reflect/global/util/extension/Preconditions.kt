package kr.co.archan.reflect.global.util.extension

import kr.co.archan.reflect.global.exception.base.ProductException

inline fun requireAndThrowProductException(value: Boolean, ex: () -> ProductException) {
    if (!value) throw ex()
}