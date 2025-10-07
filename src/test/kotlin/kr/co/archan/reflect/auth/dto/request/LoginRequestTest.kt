package kr.co.archan.reflect.auth.dto.request

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import jakarta.validation.Validation
import jakarta.validation.Validator

class LoginRequestTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    @DisplayName("LoginRequest 생성 성공")
    fun `LoginRequest 생성 성공`() {
        // given
        val email = "test@example.com"
        val password = "securePassword123!"

        // when
        val request = LoginRequest(email, password)

        // then
        assertAll("LoginRequest 생성",
            { assertEquals(email, request.email) },
            { assertEquals(password, request.password) }
        )
    }

    @Test
    @DisplayName("검증 성공 - 유효한 이메일과 패스워드")
    fun `검증 성공 - 유효한 이메일과 패스워드`() {
        // given
        val request = LoginRequest("valid@example.com", "validPass123!")

        // when
        val violations = validator.validate(request)

        // then
        assertTrue(violations.isEmpty())
    }

    @Test
    @DisplayName("이메일 검증 실패 - 이메일 공백")
    fun `이메일 검증 실패 - 이메일 공백`() {
        // given
        val email = ""
        val password = "securePassword123!"

        // when
        val violations = validator.validate(LoginRequest(email, password))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    @DisplayName("이메일 검증 실패 - @ 없음")
    fun `이메일 검증 실패 - @ 없음`() {
        // given
        val email = "invalid-email"
        val password = "securePassword123!"

        // when
        val violations = validator.validate(LoginRequest(email, password))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    @DisplayName("이메일 검증 실패 - 255자 이상")
    fun `이메일 검증 실패 - 255자 이상`() {
        // given
        val email = "testasdfsdfsasdfdasasdfddasfdddudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadfasdfdd@test.com"
        val password = "securePassword123!"

        // when
        val violations = validator.validate(LoginRequest(email, password))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    @DisplayName("패스워드 검증 실패 - 패스워드 7자 이하")
    fun `패스워드 검증 실패 - 패스워드 7자 이하`() {
        // given
        val email = "test@example.com"
        val password = "pass1!"

        // when
        val violations = validator.validate(LoginRequest(email, password))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    @DisplayName("패스워드 검증 실패 - 패스워드 65자 이상")
    fun `패스워드 검증 실패 - 패스워드 65자 이상`() {
        // given
        val email = "test@example.com"
        val password = "verylongpasswordverylongpasswordverylongpasswordverylongpaslong1!"

        // when
        val violations = validator.validate(LoginRequest(email, password))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    @DisplayName("패스워드 검증 실패 - 숫자 없음")
    fun `패스워드 검증 실패 - 숫자 없음`() {
        // given
        val email = "test@example.com"
        val password = "passwordwithoutdigit!"

        // when
        val violations = validator.validate(LoginRequest(email, password))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    @DisplayName("패스워드 검증 실패 - 특수문자 없음")
    fun `패스워드 검증 실패 - 특수문자 없음`() {
        // given
        val email = "test@example.com"
        val password = "passwordwithoutspecial123"

        // when
        val violations = validator.validate(LoginRequest(email, password))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    @DisplayName("equals - 같은 값을 가진 LoginRequest는 동등함")
    fun `equals - 같은 값을 가진 LoginRequest는 동등함`() {
        // given
        val request1 = LoginRequest("test@example.com", "password123!")
        val request2 = LoginRequest("test@example.com", "password123!")

        // when & then
        assertEquals(request1, request2)
    }

    @Test
    @DisplayName("equals - 다른 값을 가진 LoginRequest는 동등하지 않음")
    fun `equals - 다른 값을 가진 LoginRequest는 동등하지 않음`() {
        // given
        val request1 = LoginRequest("test1@example.com", "password123!")
        val request2 = LoginRequest("test2@example.com", "password123!")

        // when & then
        assertNotEquals(request1, request2)
    }

    @Test
    @DisplayName("hashCode - 같은 값을 가진 LoginRequest는 같은 hashCode")
    fun `hashCode - 같은 값을 가진 LoginRequest는 같은 hashCode`() {
        // given
        val request1 = LoginRequest("test@example.com", "password123!")
        val request2 = LoginRequest("test@example.com", "password123!")

        // when & then
        assertEquals(request1.hashCode(), request2.hashCode())
    }

    @Test
    @DisplayName("copy - 일부 프로퍼티 변경")
    fun `copy - 일부 프로퍼티 변경`() {
        // given
        val original = LoginRequest("original@example.com", "password123!")
        val newEmail = "new@example.com"

        // when
        val copied = original.copy(email = newEmail)

        // then
        assertEquals(newEmail, copied.email)
        assertEquals(original.password, copied.password)
    }

    @Test
    @DisplayName("toString - 문자열 표현 포함")
    fun `toString - 문자열 표현 포함`() {
        // given
        val request = LoginRequest("test@example.com", "password123!")

        // when
        val result = request.toString()

        // then
        assertNotNull(result)
        assertTrue(result.contains("LoginRequest"))
        assertTrue(result.contains("test@example.com"))
    }
}