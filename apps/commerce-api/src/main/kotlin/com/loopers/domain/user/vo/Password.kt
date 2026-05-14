package com.loopers.domain.user.vo

import com.loopers.domain.user.PasswordEncoder
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
class Password private constructor(
    @Column(name = "encoded_password", nullable = false)
    val encoded: String,
) {
    companion object {
        fun of(raw: String, birthday: LocalDate, encoder: PasswordEncoder): Password =
            TODO("Green 단계에서 비밀번호 정책 검증 및 인코딩 구현")
    }
}
