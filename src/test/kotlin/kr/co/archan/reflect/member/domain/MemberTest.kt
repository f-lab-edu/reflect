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
            { assertEquals(0, member.id) },
            { assertEquals(email, member.email) },
            { assertEquals(password, member.password) },
            { assertEquals(name, member.name) }
        )
    }

}