package kr.co.archan.reflect.question.domain

import jakarta.persistence.*
import kr.co.archan.reflect.global.entity.BaseEntity

@Entity
@Table(name = "question_answer")
class QuestionAnswer protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(name = "question_id", nullable = false)
    val questionId: Long,

    @Lob
    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "is_private", nullable = false)
    val isPrivate: Boolean = false,

) : BaseEntity()