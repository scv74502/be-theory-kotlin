package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import com.loopers.domain.user.vo.Password
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "users")
class UserModel(
    loginId: String,
    password: Password,
    name: String,
    birthday: LocalDate,
    email: String,
) : BaseEntity() {
    @Column(name = "login_id", unique = true, nullable = false)
    var loginId: String = loginId
        protected set

    @Embedded
    var password: Password = password
        protected set

    @Column(nullable = false)
    var name: String = name
        protected set

    @Column(nullable = false)
    var birthday: LocalDate = birthday
        protected set

    @Column(nullable = false)
    var email: String = email
        protected set

    init {
        // Red 단계: 검증 없음. Green 단계에서 loginId/name/email/birthday 포맷 검증 추가.
    }
}
