package kr.co.archan.reflect.question.domain

import jakarta.persistence.*
import kr.co.archan.reflect.global.entity.BaseEntity
import kr.co.archan.reflect.member.domain.Member

@Entity
@Table(name = "question_answer_like")
data class QuestionAnswerLike protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_answer_id", nullable = false)
    val questionAnswer: QuestionAnswer,

) : BaseEntity()