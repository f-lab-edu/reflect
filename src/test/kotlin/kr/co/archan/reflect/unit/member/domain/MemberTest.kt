package kr.co.archan.reflect.unit.member.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MemberTest : BehaviorSpec({

    context("signUp - 성공") {
        Given("유효한 이메일, 패스워드, 이름이 주어지고") {
            val email = "test@example.com"
            val password = "securePassword123!"
            val name = "홍길동"

            When("Member를 생성하면") {
                val member = Member.signUp(email, password, name)

                Then("Member가 올바르게 생성된다") {
                    member.id shouldBe 0
                    member.email shouldBe email
                    member.password shouldBe password
                    member.name shouldBe name
                }
            }
        }
    }
})
