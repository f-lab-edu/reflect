package kr.co.archan.reflect.member.repository

import kr.co.archan.reflect.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmailAndIsWithdrawnFalse(email: String): Member?
}