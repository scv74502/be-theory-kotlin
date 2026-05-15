package com.loopers.interfaces.api

import com.loopers.ApiTest
import com.loopers.domain.user.UserService
import com.loopers.domain.user.UserSteps.Companion.기본_로그인_ID
import com.loopers.domain.user.UserSteps.Companion.기본_이름
import com.loopers.domain.user.UserSteps.Companion.기본_이메일
import com.loopers.domain.user.UserSteps.Companion.사용자_회원가입
import com.loopers.domain.user.UserSteps.Companion.회원가입_요청_생성
import com.loopers.interfaces.api.user.response.SignUpResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class UserApiE2ETest
    @Autowired
    constructor(
        private val userService: UserService,
    ) : ApiTest() {
        companion object {
            private const val ENDPOINT = "/api/users"
        }

        private val responseType =
            object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

        @Test
        fun `유효한_요청이면_201_CREATED와_가입된_회원_정보를_반환한다`() {
            val request = 회원가입_요청_생성()

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
            assertThat(response.body?.data?.loginId).isEqualTo(기본_로그인_ID)
            assertThat(response.body?.data?.name).isEqualTo(기본_이름)
            assertThat(response.body?.data?.email).isEqualTo(기본_이메일)
        }

        @Test
        fun `비밀번호에_생년월일이_포함되면_400_BAD_REQUEST를_반환한다`() {
            val request = 회원가입_요청_생성(password = "Pw19900514!")

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }

        @Test
        fun `이메일_형식이_잘못되면_400_BAD_REQUEST를_반환한다`() {
            val request = 회원가입_요청_생성(email = "not-an-email")

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }

        @Test
        fun `중복_로그인ID로_가입하면_409_CONFLICT를_반환한다`() {
            userService.signUp(사용자_회원가입(loginId = "duplicate", email = "existing@example.com"))
            val request = 회원가입_요청_생성(loginId = "duplicate", email = "new@example.com")

            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        }
    }
