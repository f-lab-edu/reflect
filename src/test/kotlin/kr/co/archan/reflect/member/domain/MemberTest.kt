package kr.co.archan.reflect.member.domain

import kr.co.archan.reflect.member.exception.InvalidEmailException
import kr.co.archan.reflect.member.exception.InvalidNameException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class MemberTest {

    @Test
    @DisplayName("signUp 성공")
    fun `signUp 성공`() {
        val email = "test@example.com"
        val password = "securePassword123!"
        val name = "홍길동"

        val member = Member.signUp(email, password, name)

        assertAll("Member 생성",
            { assertNull(member.id) },
            { assertEquals(email, member.email) },
            { assertEquals(password, member.password) },
            { assertEquals(name, member.name) }
        )
    }

    @Test
    @DisplayName("signUp 실패 - 이메일 검증 실패, 이메일 공백")
    fun `signUp 실패 - 이메일 검증 실패, 이메일 공백`() {
        val email = ""
        val password = "password123"
        val name = "홍길동"

        assertThrows<InvalidEmailException> { Member.signUp(email, password, name) }
    }

    @Test
    @DisplayName("signUp 실패 - 이메일 검증 실패, @ 없음")
    fun `signUp 실패 - 이메일 검증 실패, @ 없음`() {
        val email = "a"
        val password = "password123"
        val name = "홍길동"

        assertThrows<InvalidEmailException> { Member.signUp(email, password, name) }
    }

    @Test
    @DisplayName("signUp 실패 - 이메일 검증 실패, 255자 이상")
    fun `signUp 실패 - 이메일 검증 실패, 255자 이상`() {
        val email = "testasdfsdfsasdfdasasdfddasfdddudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadfasdfdd@test.com"
        val password = "password123"
        val name = "홍길동"

        assertThrows<InvalidEmailException> { Member.signUp(email, password, name) }
    }

    @Test
    @DisplayName("signUp 실패 - 이름 검증 실패, 이름 공백")
    fun `signUp 실패 - 이름 검증 실패, 이름 공백`() {
        val email = "test@example.com"
        val password = "password123"
        val name = ""

        assertThrows<InvalidNameException> { Member.signUp(email, password, name) }
    }

    @Test
    @DisplayName("signUp 실패 - 이름 검증 실패, 20자 이상")
    fun `signUp 실패 - 이름 검증 실패`() {
        val email = "test@example.com"
        val password = "password123"
        val name = "abcdefghijklmnopqrstuvwxyz"

        assertThrows<InvalidNameException> { Member.signUp(email, password, name) }
    }
    
}