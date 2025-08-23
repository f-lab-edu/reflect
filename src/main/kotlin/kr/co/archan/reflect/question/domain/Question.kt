package kr.co.archan.reflect.question.domain

import jakarta.persistence.*
import kr.co.archan.reflect.global.entity.BaseEntity

@Entity
@Table(name = "question")
data class Question protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question", nullable = false)
    val id: Long? = null,

    @Column(name = "question_category_id", nullable = false)
    val questionCategoryId: Long,

    @Column(name = "content", length = 200, nullable = false)
    val content: String,

) : BaseEntity()