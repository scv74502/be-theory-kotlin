package com.loopers.infrastructure.user

import com.loopers.domain.BaseEntity
import com.loopers.domain.user.UserModel
import com.loopers.domain.user.vo.Password
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Column(name = "login_id", unique = true, nullable = false)
    var loginId: String,
    @Column(name = "encoded_password", nullable = false)
    var encodedPassword: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var birthday: LocalDate,
    @Column(nullable = false)
    var email: String,
) : BaseEntity() {
    fun toDomain(): UserModel = UserModel(
        id = id,
        loginId = loginId,
        password = Password.fromEncoded(encodedPassword),
        name = name,
        birthday = birthday,
        email = email,
    )

    companion object {
        fun fromDomain(user: UserModel): UserJpaEntity = UserJpaEntity(
            loginId = user.loginId,
            encodedPassword = user.password.encoded,
            name = user.name,
            birthday = user.birthday,
            email = user.email,
        )
    }
}
