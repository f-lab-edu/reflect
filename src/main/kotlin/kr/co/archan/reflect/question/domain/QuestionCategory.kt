package kr.co.archan.reflect.question.domain

import jakarta.persistence.*
import kr.co.archan.reflect.global.entity.BaseEntity

@Entity
@Table(name = "question_category")
data class QuestionCategory protected constructor (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", length = 50, nullable = false)
    val name: String,

) : BaseEntity()