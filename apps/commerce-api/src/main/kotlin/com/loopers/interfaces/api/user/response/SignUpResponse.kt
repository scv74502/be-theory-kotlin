package com.loopers.interfaces.api.user.response

import com.loopers.application.user.UserInfo
import java.time.LocalDate

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
