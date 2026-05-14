package com.loopers.interfaces.api

import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.user.UserV1Dto
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val userService: UserService,
    private val databaseCleanUp: DatabaseCleanUp,
) {
    companion object {
        private const val ENDPOINT = "/api/v1/users"
    }

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    private fun validRequest(
        loginId: String = "user1234",
        password: String = "Password1!",
        name: String = "홍길동",
        birthday: LocalDate = LocalDate.of(1990, 5, 14),
        email: String = "user@example.com",
    ) = UserV1Dto.SignUpRequest(
        loginId = loginId,
        password = password,
        name = name,
        birthday = birthday,
        email = email,
    )

    private val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.SignUpResponse>>() {}

    @DisplayName("POST /api/v1/users")
    @Nested
    inner class Post {
        @DisplayName("유효한 요청이면, 201 CREATED 응답과 가입된 회원 정보를 반환한다.")
        @Test
        fun returns201_whenSignUpRequestIsValid() {
            val request = validRequest()

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertAll(
                { assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED) },
                { assertThat(response.body?.data?.loginId).isEqualTo("user1234") },
                { assertThat(response.body?.data?.name).isEqualTo("홍길동") },
                { assertThat(response.body?.data?.email).isEqualTo("user@example.com") },
            )
        }

        @DisplayName("비밀번호에 생년월일이 포함되면, 400 BAD_REQUEST 응답을 받는다.")
        @Test
        fun returns400_whenPasswordContainsBirthday() {
            val request = validRequest(password = "Pw19900514!")

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }

        @DisplayName("이메일 형식이 잘못되면, 400 BAD_REQUEST 응답을 받는다.")
        @Test
        fun returns400_whenEmailFormatIsInvalid() {
            val request = validRequest(email = "not-an-email")

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }

        @DisplayName("이미 존재하는 로그인 ID 로 가입하면, 409 CONFLICT 응답을 받는다.")
        @Test
        fun returns409_whenLoginIdAlreadyExists() {
            userService.signUp(
                UserCommand.SignUp(
                    loginId = "duplicate",
                    rawPassword = "Password1!",
                    name = "기존회원",
                    birthday = LocalDate.of(1990, 5, 14),
                    email = "existing@example.com",
                ),
            )
            val request = validRequest(loginId = "duplicate", email = "new@example.com")

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        }
    }
}
