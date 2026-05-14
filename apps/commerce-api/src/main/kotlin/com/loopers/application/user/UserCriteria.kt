package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import java.time.LocalDate

class UserCriteria {
    data class SignUp(
        val loginId: String,
        val password: String,
        val name: String,
        val birthday: LocalDate,
        val email: String,
    ) {
        fun toCommand(): UserCommand.SignUp = UserCommand.SignUp(
            loginId = loginId,
            rawPassword = password,
            name = name,
            birthday = birthday,
            email = email,
        )
    }
}
