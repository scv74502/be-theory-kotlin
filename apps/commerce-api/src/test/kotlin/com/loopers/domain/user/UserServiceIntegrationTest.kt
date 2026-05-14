package com.loopers.domain.user

import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class UserServiceIntegrationTest @Autowired constructor(
    private val userService: UserService,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {
    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    private fun signUpCommand(
        loginId: String = "user1234",
        rawPassword: String = "Password1!",
        name: String = "홍길동",
        birthday: LocalDate = LocalDate.of(1990, 5, 14),
        email: String = "user@example.com",
    ): UserCommand.SignUp = UserCommand.SignUp(
        loginId = loginId,
        rawPassword = rawPassword,
        name = name,
        birthday = birthday,
        email = email,
    )

    @DisplayName("회원 가입을 할 때,")
    @Nested
    inner class SignUp {
        @DisplayName("유효한 요청이면, 사용자가 저장된다.")
        @Test
        fun signsUpUser_whenCommandIsValid() {
            val result = userService.signUp(signUpCommand())

            val saved = userJpaRepository.findByLoginId("user1234")
            assertAll(
                { assertThat(result.loginId).isEqualTo("user1234") },
                { assertThat(saved).isNotNull },
                { assertThat(saved?.email).isEqualTo("user@example.com") },
                { assertThat(saved?.name).isEqualTo("홍길동") },
            )
        }

        @DisplayName("이미 존재하는 로그인 ID 로 가입하면, CONFLICT 예외가 발생한다.")
        @Test
        fun throwsConflict_whenLoginIdAlreadyExists() {
            userService.signUp(signUpCommand(loginId = "duplicate"))

            val ex = assertThrows<CoreException> {
                userService.signUp(signUpCommand(loginId = "duplicate", email = "other@example.com"))
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.CONFLICT)
        }

        @DisplayName("비밀번호는 원문이 아닌 암호화된 값으로 저장된다.")
        @Test
        fun persistsEncodedPassword_notRawPassword() {
            val raw = "Password1!"
            userService.signUp(signUpCommand(rawPassword = raw))

            val saved = userJpaRepository.findByLoginId("user1234")
            assertThat(saved?.password?.encoded).isNotEqualTo(raw)
        }
    }
}
