package kr.co.archan.reflect.global.exception.types

// 잘못된 입력에 대한 예외는 모든 도메인에서 사용 가능하기 때문에
// 잘못 입력된 필드의 정보를 추상화한다

interface InvalidInputErrorSpec : ApiErrorSpec {
    val invalidField: String
}