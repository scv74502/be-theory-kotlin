package com.loopers.interfaces.api.user.response

import com.loopers.application.user.UserInfo
import java.time.LocalDate

data class MyUserResponse(
    val loginId: String,
    val name: String,
    val birthday: LocalDate,
    val email: String,
) {
    companion object {
        fun from(info: UserInfo): MyUserResponse = MyUserResponse(
            loginId = info.loginId,
            name = info.name,
            birthday = info.birthday,
            email = info.email,
        )
    }
}
