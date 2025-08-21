package kr.co.archan.reflect.member.domain

import jakarta.persistence.*
import kr.co.archan.reflect.global.entity.BaseEntity
import kr.co.archan.reflect.global.util.Validator
import kr.co.archan.reflect.global.util.extension.requireAndThrowProductException
import kr.co.archan.reflect.member.exception.InvalidEmailException
import kr.co.archan.reflect.member.exception.InvalidNameException
import kr.co.archan.reflect.member.exception.NoMemberException

@Entity
@Table(name = "member")
data class Member protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "email", length = 255, nullable = false, unique = true)
    val email: String,

    @Column(name = "password", length = 512, nullable = false)
    val password: String,

    @Column(name = "name", length = 20, nullable = false)
    val name: String,

) : BaseEntity() {

    companion object {
        fun signUp(email: String, password: String, name: String): Member {
            requireAndThrowProductException(Validator.isEmailValid(email)) { InvalidEmailException() }
            requireAndThrowProductException(Validator.isNameValid(name)) { InvalidNameException() }
            return Member(id = null, email = email, password = password, name = name)
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Member && id != null && id == other.id)

    override fun hashCode(): Int = id?.hashCode() ?: 0

}