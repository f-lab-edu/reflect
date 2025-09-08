package kr.co.archan.reflect.member.exception

import kr.co.archan.reflect.global.exception.base.ProductException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class InvalidEmailException : ProductException("이메일 입력이 잘못되었습니다")