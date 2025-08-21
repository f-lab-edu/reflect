package kr.co.archan.reflect.question.domain

import jakarta.persistence.*
import kr.co.archan.reflect.global.entity.BaseEntity
import kr.co.archan.reflect.member.domain.Member

@Entity
@Table(name = "question_answer")
class QuestionAnswer protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @Lob
    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "is_private", nullable = false)
    val isPrivate: Boolean = false,

) : BaseEntity()