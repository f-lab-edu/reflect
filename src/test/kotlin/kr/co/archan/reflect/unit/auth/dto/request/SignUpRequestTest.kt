package kr.co.archan.reflect.unit.auth.dto.request

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import jakarta.validation.Validation
import jakarta.validation.Validator
import kr.co.archan.reflect.auth.dto.request.SignUpRequest

class SignUpRequestTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    @DisplayName("SignUpRequest 생성 성공")
    fun `SignUpRequest 생성 성공`() {
        // given
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "홍길동"

        // when
        val request = SignUpRequest(email, password, name)

        // then
        assertAll("SignUpRequest 생성",
            { assertEquals(email, request.email) },
            { assertEquals(password, request.password) },
            { assertEquals(name, request.name) }
        )
    }

    @Test
    @DisplayName("검증 성공 - 유효한 이메일, 패스워드, 이름")
    fun `검증 성공 - 유효한 이메일, 패스워드, 이름`() {
        // given
        val request = SignUpRequest("valid@example.com", "validPass123!", "홍길동")

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
        val name = "홍길동"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

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
        val name = "홍길동"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

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
        val name = "홍길동"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

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
        val name = "홍길동"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

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
        val name = "홍길동"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

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
        val name = "홍길동"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

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
        val name = "홍길동"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    @DisplayName("이름 검증 실패 - 이름 공백")
    fun `이름 검증 실패 - 이름 공백`() {
        // given
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = ""

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    @DisplayName("이름 검증 실패 - 이름 21자 이상")
    fun `이름 검증 실패 - 이름 21자 이상`() {
        // given
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "가".repeat(21)

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

        // then
        assertTrue(violations.isNotEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    @DisplayName("이름 검증 성공 - 이름 1자")
    fun `이름 검증 성공 - 이름 1자`() {
        // given
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "홍"

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

        // then
        assertTrue(violations.isEmpty())
    }

    @Test
    @DisplayName("이름 검증 성공 - 이름 20자")
    fun `이름 검증 성공 - 이름 20자`() {
        // given
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "가".repeat(20)

        // when
        val violations = validator.validate(SignUpRequest(email, password, name))

        // then
        assertTrue(violations.isEmpty())
    }

    @Test
    @DisplayName("equals - 같은 값을 가진 SignUpRequest는 동등함")
    fun `equals - 같은 값을 가진 SignUpRequest는 동등함`() {
        // given
        val request1 = SignUpRequest("test@example.com", "password123!", "홍길동")
        val request2 = SignUpRequest("test@example.com", "password123!", "홍길동")

        // when & then
        assertEquals(request1, request2)
    }

    @Test
    @DisplayName("equals - 다른 값을 가진 SignUpRequest는 동등하지 않음")
    fun `equals - 다른 값을 가진 SignUpRequest는 동등하지 않음`() {
        // given
        val request1 = SignUpRequest("test1@example.com", "password123!", "홍길동")
        val request2 = SignUpRequest("test2@example.com", "password123!", "김철수")

        // when & then
        assertNotEquals(request1, request2)
    }

    @Test
    @DisplayName("hashCode - 같은 값을 가진 SignUpRequest는 같은 hashCode")
    fun `hashCode - 같은 값을 가진 SignUpRequest는 같은 hashCode`() {
        // given
        val request1 = SignUpRequest("test@example.com", "password123!", "홍길동")
        val request2 = SignUpRequest("test@example.com", "password123!", "홍길동")

        // when & then
        assertEquals(request1.hashCode(), request2.hashCode())
    }

    @Test
    @DisplayName("copy - 일부 프로퍼티 변경")
    fun `copy - 일부 프로퍼티 변경`() {
        // given
        val original = SignUpRequest("original@example.com", "password123!", "홍길동")
        val newEmail = "new@example.com"

        // when
        val copied = original.copy(email = newEmail)

        // then
        assertEquals(newEmail, copied.email)
        assertEquals(original.password, copied.password)
        assertEquals(original.name, copied.name)
    }

    @Test
    @DisplayName("toString - 문자열 표현 포함")
    fun `toString - 문자열 표현 포함`() {
        // given
        val request = SignUpRequest("test@example.com", "password123!", "홍길동")

        // when
        val result = request.toString()

        // then
        assertNotNull(result)
        assertTrue(result.contains("SignUpRequest"))
        assertTrue(result.contains("test@example.com"))
        assertTrue(result.contains("홍길동"))
    }
}