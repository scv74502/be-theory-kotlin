package com.loopers.domain.user

import com.loopers.domain.user.vo.Password
import com.loopers.interfaces.api.user.request.SignUpRequest
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class UserSteps {
    companion object {
        const val 기본_로그인_ID: String = "user1234"
        const val 기본_비밀번호: String = "Password1!"
        const val 기본_이름: String = "홍길동"
        const val 기본_이메일: String = "user@example.com"
        val 기본_생년월일: LocalDate = LocalDate.of(1990, 5, 14)

        fun 기본_인코더(): PasswordEncoder = mockk {
            every { encode(any()) } answers { "ENC(" + firstArg<String>() + ")" }
        }

        fun 비밀번호_생성(
            rawPassword: String = 기본_비밀번호,
            birthday: LocalDate = 기본_생년월일,
            encoder: PasswordEncoder = 기본_인코더(),
        ): Password = Password.of(rawPassword, birthday, encoder)

        fun 회원_도메인_생성(
            loginId: String = 기본_로그인_ID,
            rawPassword: String = 기본_비밀번호,
            name: String = 기본_이름,
            birthday: LocalDate = 기본_생년월일,
            email: String = 기본_이메일,
            encoder: PasswordEncoder = 기본_인코더(),
        ): UserModel = UserModel(
            loginId = loginId,
            password = 비밀번호_생성(rawPassword, birthday, encoder),
            name = name,
            birthday = birthday,
            email = email,
        )

        fun 회원가입_커맨드_생성(
            loginId: String = 기본_로그인_ID,
            rawPassword: String = 기본_비밀번호,
            name: String = 기본_이름,
            birthday: LocalDate = 기본_생년월일,
            email: String = 기본_이메일,
        ): UserCommand.SignUp = UserCommand.SignUp(
            loginId = loginId,
            rawPassword = rawPassword,
            name = name,
            birthday = birthday,
            email = email,
        )

        fun 회원가입_요청_생성(
            loginId: String = 기본_로그인_ID,
            password: String = 기본_비밀번호,
            name: String = 기본_이름,
            birthday: LocalDate = 기본_생년월일,
            email: String = 기본_이메일,
        ): SignUpRequest = SignUpRequest(
            loginId = loginId,
            password = password,
            name = name,
            birthday = birthday,
            email = email,
        )
    }
}
