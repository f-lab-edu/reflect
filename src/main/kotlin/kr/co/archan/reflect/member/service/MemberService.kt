package kr.co.archan.reflect.member.service

import kr.co.archan.reflect.member.domain.Member
import kr.co.archan.reflect.member.exception.common.MemberAlreadyExistsException
import kr.co.archan.reflect.member.exception.common.MemberNotFoundException
import kr.co.archan.reflect.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService (
    private val memberRepository: MemberRepository
){

    @Transactional(propagation = Propagation.MANDATORY)
    fun saveNewMember(member: Member) : Member {

        if (memberRepository.existsByEmailAndIsWithdrawnFalse(member.email)) {
            throw MemberAlreadyExistsException()
        }

        return memberRepository.save(member)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun getMember(email: String) : Member {
        return memberRepository.findByEmailAndIsWithdrawnFalse(email) ?: throw MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
    }
}