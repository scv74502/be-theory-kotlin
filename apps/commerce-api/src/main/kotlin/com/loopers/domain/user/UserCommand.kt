package com.loopers.domain.user

import java.time.LocalDate

class UserCommand {
    data class SignUp(
        val loginId: String,
        val rawPassword: String,
        val name: String,
        val birthday: LocalDate,
        val email: String,
    )
}
