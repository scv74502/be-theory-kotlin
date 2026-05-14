package com.loopers.interfaces.api.user.request

import com.loopers.application.user.UserCriteria
import java.time.LocalDate

data class SignUpRequest(
    val loginId: String,
    val password: String,
    val name: String,
    val birthday: LocalDate,
    val email: String,
) {
    fun toCriteria(): UserCriteria.SignUp = UserCriteria.SignUp(
        loginId = loginId,
        password = password,
        name = name,
        birthday = birthday,
        email = email,
    )
}
