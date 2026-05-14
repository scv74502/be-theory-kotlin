package com.loopers.domain.user

import com.loopers.domain.user.vo.Password
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class UserModelTest {
    private val encoder: PasswordEncoder = mockk {
        every { encode(any()) } answers { "ENC(" + firstArg<String>() + ")" }
    }
    private val defaultBirthday: LocalDate = LocalDate.of(1990, 5, 14)
    private val defaultRawPassword = "Password1!"

    private fun buildUser(
        loginId: String = "user1234",
        rawPassword: String = defaultRawPassword,
        name: String = "홍길동",
        birthday: LocalDate = defaultBirthday,
        email: String = "user@example.com",
    ): UserModel = UserModel(
        loginId = loginId,
        password = Password.of(rawPassword, birthday, encoder),
        name = name,
        birthday = birthday,
        email = email,
    )

    @DisplayName("회원 모델을 생성할 때,")
    @Nested
    inner class Create {
        @DisplayName("모든 필드가 유효하면, 정상적으로 생성된다.")
        @Test
        fun creates_whenAllFieldsAreValid() {
            val user = buildUser()
            assertAll(
                { assertThat(user.loginId).isEqualTo("user1234") },
                { assertThat(user.name).isEqualTo("홍길동") },
                { assertThat(user.birthday).isEqualTo(defaultBirthday) },
                { assertThat(user.email).isEqualTo("user@example.com") },
                { assertThat(user.password.encoded).isEqualTo("ENC($defaultRawPassword)") },
            )
        }

        @DisplayName("로그인 ID 가 영숫자 외 문자를 포함하면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenLoginIdContainsInvalidCharacters() {
            val ex = assertThrows<CoreException> { buildUser(loginId = "한글ID!") }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("로그인 ID 가 4자 미만이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenLoginIdTooShort() {
            val ex = assertThrows<CoreException> { buildUser(loginId = "abc") }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("로그인 ID 가 20자 초과면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenLoginIdTooLong() {
            val ex = assertThrows<CoreException> { buildUser(loginId = "a".repeat(21)) }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("이름이 공백이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenNameIsBlank() {
            val ex = assertThrows<CoreException> { buildUser(name = "   ") }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("이메일 형식이 잘못되면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenEmailFormatInvalid() {
            val ex = assertThrows<CoreException> { buildUser(email = "not-an-email") }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("생년월일이 미래 일자면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenBirthdayInFuture() {
            val ex = assertThrows<CoreException> {
                buildUser(birthday = LocalDate.now().plusDays(1))
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }
    }

    @DisplayName("비밀번호를 생성할 때,")
    @Nested
    inner class CreatePassword {
        @DisplayName("8자 미만이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordTooShort() {
            val ex = assertThrows<CoreException> {
                Password.of("Ab1!abc", defaultBirthday, encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("16자 초과면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordTooLong() {
            val ex = assertThrows<CoreException> {
                Password.of("Ab1!" + "a".repeat(14), defaultBirthday, encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("대문자가 없으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordHasNoUppercase() {
            val ex = assertThrows<CoreException> {
                Password.of("password1!", defaultBirthday, encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("소문자가 없으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordHasNoLowercase() {
            val ex = assertThrows<CoreException> {
                Password.of("PASSWORD1!", defaultBirthday, encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("숫자가 없으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordHasNoDigit() {
            val ex = assertThrows<CoreException> {
                Password.of("Password!!", defaultBirthday, encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("특수문자가 없으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordHasNoSpecialChar() {
            val ex = assertThrows<CoreException> {
                Password.of("Password11", defaultBirthday, encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("비밀번호에 생년월일(yyyyMMdd) 토큰이 포함되면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordContainsBirthdayYyyyMmDd() {
            val ex = assertThrows<CoreException> {
                Password.of("Pw19900514!", LocalDate.of(1990, 5, 14), encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("비밀번호에 생년월일(yyMMdd) 토큰이 포함되면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordContainsBirthdayYyMmDd() {
            val ex = assertThrows<CoreException> {
                Password.of("Pw900514A!", LocalDate.of(1990, 5, 14), encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("비밀번호에 생년월일(MMdd) 토큰이 포함되면, BAD_REQUEST 예외가 발생한다.")
        @Test
        fun throwsBadRequest_whenPasswordContainsBirthdayMmDd() {
            val ex = assertThrows<CoreException> {
                Password.of("Pw0514AB!", LocalDate.of(1990, 5, 14), encoder)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }
    }
}
