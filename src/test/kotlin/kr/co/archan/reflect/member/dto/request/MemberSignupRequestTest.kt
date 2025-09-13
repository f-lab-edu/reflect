package kr.co.archan.reflect.member.dto.request

import kr.co.archan.reflect.global.exception.common.InvalidInputException
import kr.co.archan.reflect.member.exception.types.MemberInvalidInputField
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import jakarta.validation.Validation
import jakarta.validation.Validator

class MemberSignupRequestTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    private fun validateOrThrow(req: MemberSignupRequest) {
        val violations = validator.validate(req)
        if (violations.isNotEmpty()) {
            val specs = violations.map { v ->
                when (v.propertyPath.toString()) {
                    "email" -> MemberInvalidInputField.EMAIL
                    "password" -> MemberInvalidInputField.PASSWORD
                    "name" -> MemberInvalidInputField.NAME
                    else -> MemberInvalidInputField.UNKNOWN
                }
            }
            throw InvalidInputException(specs)
        }
    }

    @Test
    @DisplayName("MemberSignupRequest 생성 성공")
    fun `MemberSignupRequest 생성 성공`() {
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "홍길동"

        val request = MemberSignupRequest(email, password, name)

        assertAll("MemberSignupRequest 생성",
            { assertEquals(email, request.email) },
            { assertEquals(password, request.password) },
            { assertEquals(name, request.name) }
        )
    }

    @Test
    @DisplayName("이메일 검증 실패, 이메일 공백")
    fun `이메일 검증 실패, 이메일 공백`() {
        val email = ""
        val password = "securePassword123!"
        val name = "홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.EMAIL))
    }

    @Test
    @DisplayName("이메일 검증 실패, @ 없음")
    fun `이메일 검증 실패, @ 없음`() {
        val email = "invalid-email"
        val password = "securePassword123!"
        val name = "홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.EMAIL))
    }

    @Test
    @DisplayName("이메일 검증 실패, 255자 이상")
    fun `이메일 검증 실패, 255자 이상`() {
        val email = "testasdfsdfsasdfdasasdfddasfdddudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadfasdfdd@test.com"
        val password = "securePassword123!"
        val name = "홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.EMAIL))
    }

    @Test
    @DisplayName("이름 검증 실패, 이름 공백")
    fun `이름 검증 실패, 이름 공백`() {
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = ""

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.NAME))
    }

    @Test
    @DisplayName("이름 검증 실패, 이름 21자 이상")
    fun `이름 검증 실패, 이름 21자 이상`() {
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "홍길동홍길동홍길동홍길동홍길동홍길동홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.NAME))
    }

    @Test
    @DisplayName("패스워드 검증 실패, 패스워드 7자 이하")
    fun `패스워드 검증 실패, 패스워드 7자 이하`() {
        val email = "test@example.com"
        val password = "pass1!"
        val name = "홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.PASSWORD))
    }

    @Test
    @DisplayName("패스워드 검증 실패, 패스워드 65자 이상")
    fun `패스워드 검증 실패, 패스워드 65자 이상`() {
        val email = "test@example.com"
        val password = "verylongpasswordverylongpasswordverylongpasswordverylongpaslong1!"
        val name = "홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.PASSWORD))
    }

    @Test
    @DisplayName("패스워드 검증 실패, 숫자 없음")
    fun `패스워드 검증 실패, 숫자 없음`() {
        val email = "test@example.com"
        val password = "passwordwithoutdigit!"
        val name = "홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.PASSWORD))
    }

    @Test
    @DisplayName("패스워드 검증 실패, 특수문자 없음")
    fun `패스워드 검증 실패, 특수문자 없음`() {
        val email = "test@example.com"
        val password = "passwordwithoutspecial123"
        val name = "홍길동"

        val exception = assertThrows<InvalidInputException> { validateOrThrow(MemberSignupRequest(email, password, name)) }
        assertTrue(exception.specs.contains(MemberInvalidInputField.PASSWORD))
    }
}