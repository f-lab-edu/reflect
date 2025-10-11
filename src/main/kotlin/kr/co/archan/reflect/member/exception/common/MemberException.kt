package kr.co.archan.reflect.member.exception.common

import kr.co.archan.reflect.global.exception.base.ApiException
import kr.co.archan.reflect.member.exception.types.MemberErrorCode

class MemberException(errorCode: MemberErrorCode) : ApiException(errorCode)

