package kr.co.archan.reflect.unit.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.archan.reflect.global.properties.CryptoProperties
import kr.co.archan.reflect.global.util.Crypto
import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.exception.common.MemberException
import kr.co.archan.reflect.member.exception.types.MemberErrorCode
import kr.co.archan.reflect.member.repository.MemberRepository
import kr.co.archan.reflect.member.service.MemberService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

class MemberServiceTest : BehaviorSpec({
    
    fun createTestComponents() = object {
        val memberRepository = mockk<MemberRepository>()
        val memberService = MemberService(memberRepository)
        private val testPepperKey = "test-pepper-key"
        val cryptoProperties = CryptoProperties(
            hashKey = "test-secret-key",
            pepperKey = testPepperKey
        )
        // 실제 Argon2PasswordEncoder 인스턴스 사용
        val passwordEncoder = Argon2PasswordEncoder(16, 32, 1, 64 * 1024, 3)
        val crypto = Crypto(cryptoProperties, passwordEncoder)
    }

    context("getMember - 이메일로 회원 조회 성공 (탈퇴하지 않은 회원만)") {
        Given("탈퇴하지 않은 회원이 존재하고") {
            val tc = createTestComponents()
            
            val email = "test@example.com"
            val expectedMember = Member.signUp(
                email = email,
                hashedPassword = tc.crypto.hashPassword("password"),
                name = "홍길동"
            )
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns expectedMember

            When("이메일로 회원을 조회하면") {
                val result = tc.memberService.getMember(email)

                Then("회원 정보가 반환된다") {
                    result shouldNotBe null
                    result.email shouldBe expectedMember.email
                    result.password shouldBe expectedMember.password
                    result.name shouldBe expectedMember.name
                    result.isWithdrawn shouldBe false
                    verify(exactly = 1) { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) }
                }
            }
        }
    }

    context("getMember - 존재하지 않는 이메일로 조회 시 MemberNotFoundException 발생") {
        Given("존재하지 않는 이메일이 주어지고") {
            val tc = createTestComponents()
            
            val email = "notfound@example.com"
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

            When("이메일로 회원을 조회하려고 시도하면") {
                Then("MemberNotFoundException이 발생한다") {
                    val exception = shouldThrow<MemberException> {
                        tc.memberService.getMember(email)
                    }
                    exception.apiErrorSpec shouldBe MemberErrorCode.MEMBER_NOT_FOUND
                    verify(exactly = 1) { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) }
                }
            }
        }
    }

    context("getMember - 대소문자가 다른 이메일은 다른 회원으로 간주") {
        Given("대소문자가 다른 이메일을 가진 두 회원이 있고") {
            val tc = createTestComponents()
            
            val email1 = "Test@example.com"
            val email2 = "test@example.com"
            val member1 = Member.signUp(email1, tc.crypto.hashPassword("password1"), "회원1")
            val member2 = Member.signUp(email2, tc.crypto.hashPassword("password2"), "회원2")
            
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email1) } returns member1
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email2) } returns member2

            When("각각 조회하면") {
                val result1 = tc.memberService.getMember(email1)
                val result2 = tc.memberService.getMember(email2)

                Then("서로 다른 회원이 조회된다") {
                    result1.email shouldBe email1
                    result2.email shouldBe email2
                    result1.name shouldBe "회원1"
                    result2.name shouldBe "회원2"
                }
            }
        }
    }

    context("getMember - 탈퇴한 회원은 조회되지 않음") {
        Given("탈퇴한 회원이 있고") {
            val tc = createTestComponents()
            
            val email = "withdrawn@example.com"
            every { tc.memberRepository.findByEmailAndIsWithdrawnFalse(email) } returns null

            When("이메일로 회원을 조회하려고 시도하면") {
                Then("MemberNotFoundException이 발생한다") {
                    val exception = shouldThrow<MemberException> {
                        tc.memberService.getMember(email)
                    }
                    exception.apiErrorSpec shouldBe MemberErrorCode.MEMBER_NOT_FOUND
                }
            }
        }
    }
})
