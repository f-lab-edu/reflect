package kr.co.archan.reflect.member.exception

import kr.co.archan.reflect.global.exception.base.ProductException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class InvalidNameException : ProductException("이름 형식이 잘못되었습니다")