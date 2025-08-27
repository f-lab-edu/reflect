package kr.co.archan.reflect.global.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class ValidatorTest {

    @Test
    @DisplayName("isEmailValid 성공 - 일반적인 이메일")
    fun `isEmailValid 성공 - 일반적인 이메일`() {
        val email = "test@example.com"

        assertTrue(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 성공 - 숫자와 특수문자 포함")
    fun `isEmailValid 성공 - 숫자와 특수문자 포함`() {
        val email = "user.name+tag123@example.co.kr"

        assertTrue(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 이메일 공백")
    fun `isEmailValid 실패 - 이메일 공백`() {
        val email = ""

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - @ 없음")
    fun `isEmailValid 실패 - @ 없음`() {
        val email = "testexample.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 255자 이상")
    fun `isEmailValid 실패 - 255자 이상`() {
        val email =
            "testasdfsdfsasdfdasasdfddasfdddudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadfasdfdd@test.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 공백 포함")
    fun `isEmailValid 실패 - 공백 포함`() {
        val email = "test @example.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 로컬파트 없음")
    fun `isEmailValid 실패 - 로컬파트 없음`() {
        val email = "@example.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 도메인 없음")
    fun `isEmailValid 실패 - 도메인 없음`() {
        val email = "test@"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 도메인에 점으로 시작")
    fun `isEmailValid 실패 - 도메인에 점으로 시작`() {
        val email = "test@.example.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 도메인에 점으로 끝남")
    fun `isEmailValid 실패 - 도메인에 점으로 끝남`() {
        val email = "test@example.com."

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 연속된 점")
    fun `isEmailValid 실패 - 연속된 점`() {
        val email = "test@example..com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - TLD 1글자")
    fun `isEmailValid 실패 - TLD 1글자`() {
        val email = "test@example.c"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 로컬파트 65자 이상")
    fun `isEmailValid 실패 - 로컬파트 65자 이상`() {
        val email = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklm@example.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 허용되지 않는 문자")
    fun `isEmailValid 실패 - 허용되지 않는 문자`() {
        val email = "test@#example.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 도메인 레이블 수 부족")
    fun `isEmailValid 실패 - 도메인 레이블 수 부족`() {
        val email = "test@example"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - TLD에 숫자 포함")
    fun `isEmailValid 실패 - TLD에 숫자 포함`() {
        val email = "test@example.co2"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 로컬파트에 허용되지 않는 문자")
    fun `isEmailValid 실패 - 로컬파트에 허용되지 않는 문자`() {
        val email = "test@domain@example.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 도메인에 허용되지 않는 문자")
    fun `isEmailValid 실패 - 도메인에 허용되지 않는 문자`() {
        val email = "test@exam_ple.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - 여러 개의 @ 기호")
    fun `isEmailValid 실패 - 여러 개의 @ 기호`() {
        val email = "test@@example.com"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 실패 - @ 기호만")
    fun `isEmailValid 실패 - @ 기호만`() {
        val email = "@"

        assertFalse(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 성공 - 경계값 254자")
    fun `isEmailValid 성공 - 경계값 254자`() {
        // 정확히 254자 이메일 구성
        val localPart = "a".repeat(64)  // 64자 (최대값)
        val domainPart = "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(57) + ".com"  // 188자
        val email = "$localPart@$domainPart"  // 64 + 1 + 188 = 253자

        assertTrue(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 성공 - 로컬파트 경계값 64자")
    fun `isEmailValid 성공 - 로컬파트 경계값 64자`() {
        val email = "a".repeat(64) + "@example.com"

        assertTrue(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isEmailValid 성공 - TLD 경계값 2자")
    fun `isEmailValid 성공 - TLD 경계값 2자`() {
        val email = "test@example.co"

        assertTrue(Validator.isEmailValid(email))
    }

    @Test
    @DisplayName("isNameValid 성공 - 일반적인 이름")
    fun `isNameValid 성공 - 일반적인 이름`() {
        val name = "홍길동"

        assertTrue(Validator.isNameValid(name))
    }

    @Test
    @DisplayName("isNameValid 성공 - 영문 이름")
    fun `isNameValid 성공 - 영문 이름`() {
        val name = "John Doe"

        assertTrue(Validator.isNameValid(name))
    }

    @Test
    @DisplayName("isNameValid 성공 - 20자 이름")
    fun `isNameValid 성공 - 20자 이름`() {
        val name = "가나다라마바사아자차카타파하가나다라마바"

        assertTrue(Validator.isNameValid(name))
    }

    @Test
    @DisplayName("isNameValid 실패 - 이름 공백")
    fun `isNameValid 실패 - 이름 공백`() {
        val name = ""

        assertFalse(Validator.isNameValid(name))
    }

    @Test
    @DisplayName("isNameValid 실패 - 이름 공백만")
    fun `isNameValid 실패 - 이름 공백만`() {
        val name = "   "

        assertFalse(Validator.isNameValid(name))
    }

    @Test
    @DisplayName("isNameValid 실패 - 21자 이상")
    fun `isNameValid 실패 - 21자 이상`() {
        val name = "가나다라마바사아자차카타파하가나다라마바사"

        assertFalse(Validator.isNameValid(name))
    }

}