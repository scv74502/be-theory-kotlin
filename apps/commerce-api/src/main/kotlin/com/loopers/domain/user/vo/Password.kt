package com.loopers.domain.user.vo

import com.loopers.domain.user.PasswordEncoder
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Embeddable
class Password private constructor(
    @Column(name = "encoded_password", nullable = false)
    val encoded: String,
) {
    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 16
        private val BIRTHDAY_TOKEN_PATTERNS = listOf("yyyyMMdd", "yyMMdd", "MMdd")

        fun of(raw: String, birthday: LocalDate, encoder: PasswordEncoder): Password {
            if (raw.length !in MIN_LENGTH..MAX_LENGTH) {
                throw CoreException(ErrorType.BAD_REQUEST, "비밀번호는 $MIN_LENGTH~${MAX_LENGTH}자여야 합니다.")
            }
            if (raw.any { it.isWhitespace() }) {
                throw CoreException(ErrorType.BAD_REQUEST, "비밀번호에 공백을 포함할 수 없습니다.")
            }
            if (!raw.any { it.isUpperCase() }) {
                throw CoreException(ErrorType.BAD_REQUEST, "비밀번호는 영문 대문자를 포함해야 합니다.")
            }
            if (!raw.any { it.isLowerCase() }) {
                throw CoreException(ErrorType.BAD_REQUEST, "비밀번호는 영문 소문자를 포함해야 합니다.")
            }
            if (!raw.any { it.isDigit() }) {
                throw CoreException(ErrorType.BAD_REQUEST, "비밀번호는 숫자를 포함해야 합니다.")
            }
            if (!raw.any { !it.isLetterOrDigit() }) {
                throw CoreException(ErrorType.BAD_REQUEST, "비밀번호는 특수문자를 포함해야 합니다.")
            }
            val containsBirthdayToken = BIRTHDAY_TOKEN_PATTERNS.any { pattern ->
                raw.contains(birthday.format(DateTimeFormatter.ofPattern(pattern)))
            }
            if (containsBirthdayToken) {
                throw CoreException(ErrorType.BAD_REQUEST, "비밀번호에 생년월일을 포함할 수 없습니다.")
            }
            return Password(encoder.encode(raw))
        }
    }
}
