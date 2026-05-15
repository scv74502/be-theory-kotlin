package com.loopers.domain.user

import com.loopers.domain.user.vo.Password
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.time.LocalDate

class UserModel(
    val id: Long = 0,
    val loginId: String,
    val password: Password,
    val name: String,
    val birthday: LocalDate,
    val email: String,
) {

    init {
        if (!LOGIN_ID_PATTERN.matches(loginId)) {
            throw CoreException(ErrorType.BAD_REQUEST, "로그인 ID는 영문/숫자 4~20자여야 합니다.")
        }
        if (name.isBlank() || name.length > NAME_MAX_LENGTH) {
            throw CoreException(ErrorType.BAD_REQUEST, "이름은 공백이 아닌 1~${NAME_MAX_LENGTH}자여야 합니다.")
        }
        if (!EMAIL_PATTERN.matches(email)) {
            throw CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.")
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw CoreException(ErrorType.BAD_REQUEST, "생년월일은 미래일 수 없습니다.")
        }
    }

    companion object {
        private val LOGIN_ID_PATTERN = Regex("^[A-Za-z0-9]{4,20}$")
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private const val NAME_MAX_LENGTH = 50
    }
}
