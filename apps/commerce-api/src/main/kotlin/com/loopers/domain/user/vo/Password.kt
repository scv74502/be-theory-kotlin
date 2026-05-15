package com.loopers.domain.user.vo

import com.loopers.domain.user.InvalidPasswordException
import com.loopers.domain.user.PasswordEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Password private constructor(
    val encoded: String,
) {
    companion object {
        private val PASSWORD_REGEX =
            Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z0-9!@#$%^&*(),.?\":{}|<>]{8,16}$")
        private val BIRTHDAY_TOKEN_PATTERNS = listOf("yyyyMMdd", "yyMMdd", "MMdd")

        fun of(raw: String, birthday: LocalDate, encoder: PasswordEncoder): Password {
            validate(raw, birthday)
            return Password(encoder.encode(raw))
        }

        fun fromEncoded(encoded: String): Password = Password(encoded)

        private fun validate(raw: String, birthday: LocalDate) {
            validateFormat(raw)
            validateNotContainsBirthday(raw, birthday)
        }

        private fun validateFormat(raw: String) {
            validate(
                PASSWORD_REGEX.matches(raw),
                "비밀번호는 8~16자의 영문 대문자, 소문자, 숫자, 특수문자를 포함해야 하며 공백을 포함할 수 없습니다.",
            )
        }

        private fun validateNotContainsBirthday(raw: String, birthday: LocalDate) {
            validate(!containsBirthdayToken(raw, birthday), "비밀번호에 생년월일을 포함할 수 없습니다.")
        }

        private fun containsBirthdayToken(raw: String, birthday: LocalDate): Boolean =
            BIRTHDAY_TOKEN_PATTERNS.any { pattern ->
                raw.contains(birthday.format(DateTimeFormatter.ofPattern(pattern)))
            }

        private fun validate(condition: Boolean, message: String) {
            if (!condition) {
                throw InvalidPasswordException(message)
            }
        }
    }
}
