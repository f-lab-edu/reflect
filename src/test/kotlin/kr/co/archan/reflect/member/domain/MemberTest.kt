package kr.co.archan.reflect.member.domain

import jakarta.validation.Validation
import jakarta.validation.Validator

import kr.co.archan.reflect.global.exception.common.InvalidInputException
import kr.co.archan.reflect.member.dto.request.MemberSignupRequest
import kr.co.archan.reflect.member.exception.types.MemberInvalidInputField
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class MemberTest {

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
    @DisplayName("signUp 성공")
    fun `signUp 성공`() {
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "홍길동"

        val member = Member.signUp(email, password, name)

        assertAll("Member 생성",
            { assertEquals(0, member.id) },
            { assertEquals(email, member.email) },
            { assertEquals(password, member.password) },
            { assertEquals(name, member.name) }
        )
    }

    @Test
    @DisplayName("MemberSignupRequest를 통한 이메일 검증 실패")
    fun `MemberSignupRequest를 통한 이메일 검증 실패`() {
        val email = "invalid-email"
        val password = "securePassword123!"
        val name = "홍길동"
        val exception = assertThrows<InvalidInputException> {
            validateOrThrow(MemberSignupRequest(email, password, name))
        }
        assertTrue(exception.specs.contains(MemberInvalidInputField.EMAIL))
    }

    @Test
    @DisplayName("MemberSignupRequest를 통한 이름 검증 실패")
    fun `MemberSignupRequest를 통한 이름 검증 실패`() {
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = ""

        val exception = assertThrows<InvalidInputException> {
            validateOrThrow(MemberSignupRequest(email, password, name))
        }
        assertTrue(exception.specs.contains(MemberInvalidInputField.NAME))
    }

    @Test
    @DisplayName("MemberSignupRequest를 통한 패스워드 검증 실패")
    fun `MemberSignupRequest를 통한 패스워드 검증 실패`() {
        val email = "test@example.com"
        val password = "weak"
        val name = "홍길동"
        val exception = assertThrows<InvalidInputException> {
            validateOrThrow(MemberSignupRequest(email, password, name))
        }
        assertTrue(exception.specs.contains(MemberInvalidInputField.PASSWORD))
    }

    @Test
    @DisplayName("MemberSignupRequest를 통한 검증 성공")
    fun `MemberSignupRequest를 통한 검증 성공`() {
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

}