package kr.co.archan.reflect.member.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.exception.common.MemberNotFoundException
import kr.co.archan.reflect.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach

class MemberServiceTest {

    private lateinit var memberService: MemberService
    private lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun setUp() {
        memberRepository = mockk()
        memberService = MemberService(memberRepository)
    }

    @Test
    @DisplayName("getMember - 이메일로 회원 조회 성공 (탈퇴하지 않은 회원만)")
    fun `getMember - 이메일로 회원 조회 성공 (탈퇴하지 않은 회원만)`() {
        // given
        val email = "test@example.com"
        val expectedMember = Member.signUp(
            email = email,
            password = "hashedPassword123",
            name = "홍길동"
        )
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns expectedMember

        // when
        val result = memberService.getMember(email)

        // then
        assertNotNull(result)
        assertEquals(expectedMember.email, result.email)
        assertEquals(expectedMember.password, result.password)
        assertEquals(expectedMember.name, result.name)
        assertFalse(result.isWithdrawn)
        verify(exactly = 1) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
    }

    @Test
    @DisplayName("getMember - 존재하지 않는 이메일로 조회 시 MemberNotFoundException 발생")
    fun `getMember - 존재하지 않는 이메일로 조회 시 MemberNotFoundException 발생`() {
        // given
        val email = "notfound@example.com"
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

        // when & then
        val exception = assertThrows<MemberNotFoundException> {
            memberService.getMember(email)
        }

        assertNotNull(exception)
        verify(exactly = 1) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
    }

    @Test
    @DisplayName("getMember - 대소문자가 다른 이메일은 다른 회원으로 간주")
    fun `getMember - 대소문자가 다른 이메일은 다른 회원으로 간주`() {
        // given
        val lowerEmail = "user@example.com"
        val upperEmail = "USER@EXAMPLE.COM"
        
        val lowerMember = Member.signUp(lowerEmail, "password1", "소문자")
        val upperMember = Member.signUp(upperEmail, "password2", "대문자")
        
        every { memberRepository.findByEmailAndIsWithdrawnFalse(lowerEmail) } returns lowerMember
        every { memberRepository.findByEmailAndIsWithdrawnFalse(upperEmail) } returns upperMember

        // when
        val lowerResult = memberService.getMember(lowerEmail)
        val upperResult = memberService.getMember(upperEmail)

        // then
        assertEquals(lowerEmail, lowerResult.email)
        assertEquals(upperEmail, upperResult.email)
        assertNotEquals(lowerResult.email, upperResult.email)
    }

    @Test
    @DisplayName("getMember - 빈 문자열로 조회 시 null 반환하여 예외 발생")
    fun `getMember - 빈 문자열로 조회 시 null 반환하여 예외 발생`() {
        // given
        val email = ""
        every { memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

        // when & then
        assertThrows<MemberNotFoundException> {
            memberService.getMember(email)
        }

        verify(exactly = 1) { memberRepository.findByEmailAndIsWithdrawnFalse(email) }
    }
}