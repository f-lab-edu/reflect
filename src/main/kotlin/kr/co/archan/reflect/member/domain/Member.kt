package kr.co.archan.reflect.member.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
data class Member protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0L,

    @Column(name = "email", length = 254, nullable = false, unique = true)
    val email: String,

    @Column(name = "password", length = 512, nullable = false)
    val password: String,

    @Column(name = "name", length = 20, nullable = false)
    val name: String,

    @Column(name = "is_withdrawn", nullable = false)
    val isWithdrawn: Boolean = false,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    var updatedAt: LocalDateTime? = null

) {

    companion object {
        fun signUp(email: String, password: String, name: String): Member {
            return Member(email = email, password = password, name = name)
        }
    }

}