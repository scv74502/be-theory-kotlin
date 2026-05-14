package com.loopers.interfaces.api.user

import com.loopers.application.user.UserCriteria
import com.loopers.application.user.UserInfo
import java.time.LocalDate

class UserV1Dto {
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

    data class SignUpResponse(
        val id: Long,
        val loginId: String,
        val name: String,
        val birthday: LocalDate,
        val email: String,
    ) {
        companion object {
            fun from(info: UserInfo): SignUpResponse = SignUpResponse(
                id = info.id,
                loginId = info.loginId,
                name = info.name,
                birthday = info.birthday,
                email = info.email,
            )
        }
    }
}
