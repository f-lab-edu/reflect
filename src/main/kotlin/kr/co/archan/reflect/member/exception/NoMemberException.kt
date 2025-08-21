package kr.co.archan.reflect.member.exception

import kr.co.archan.reflect.global.exception.ProductException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NoMemberException : ProductException("회원이 없습니다.")