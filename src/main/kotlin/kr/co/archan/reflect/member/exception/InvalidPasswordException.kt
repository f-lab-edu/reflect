package kr.co.archan.reflect.member.exception

import kr.co.archan.reflect.global.exception.ProductException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class InvalidPasswordException : ProductException("패스워드는 8자 이상 64자 이하로, 숫자와 특수문자(공백 제외)를 포함해야 합니다")
