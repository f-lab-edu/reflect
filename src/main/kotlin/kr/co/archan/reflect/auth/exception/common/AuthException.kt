package kr.co.archan.reflect.auth.exception.common

import kr.co.archan.reflect.auth.exception.types.AuthErrorCode
import kr.co.archan.reflect.global.exception.base.ApiException

class AuthException(errorCode: AuthErrorCode) : ApiException(errorCode)

