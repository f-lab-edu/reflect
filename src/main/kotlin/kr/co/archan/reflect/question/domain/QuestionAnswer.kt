package kr.co.archan.reflect.question.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "question_answer")
@EntityListeners(AuditingEntityListener::class)
class QuestionAnswer protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0L,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(name = "question_id", nullable = false)
    val questionId: Long,

    @Lob
    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "is_private", nullable = false)
    val isPrivate: Boolean = false,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    var updatedAt: LocalDateTime? = null

)