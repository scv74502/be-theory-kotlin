package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.interfaces.api.ApiResponse
import com.loopers.interfaces.api.user.request.SignUpRequest
import com.loopers.interfaces.api.user.response.MyUserResponse
import com.loopers.interfaces.api.user.response.SignUpResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userFacade: UserFacade,
) : UserApiSpec {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun signUp(
        @Valid @RequestBody request: SignUpRequest,
    ): ApiResponse<SignUpResponse> {
        return userFacade.signUp(request.toCommand())
            .let { SignUpResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("/me")
    override fun getMe(
        @RequestHeader(name = LOGIN_ID_HEADER, required = false) loginId: String?,
        @RequestHeader(name = LOGIN_PW_HEADER, required = false) rawPassword: String?,
    ): ApiResponse<MyUserResponse> {
        if (loginId == null || rawPassword == null) {
            throw CoreException(ErrorType.UNAUTHORIZED)
        }
        val user = userFacade.getMe(loginId, rawPassword)
        return ApiResponse.success(
            MyUserResponse(
                loginId = user.loginId,
                name = maskName(user.name),
                birthday = user.birthday,
                email = user.email,
            ),
        )
    }

    private fun maskName(name: String): String =
        if (name.length == 1) {
            "*"
        } else {
            name.dropLast(1) + "*"
        }

    companion object {
        private const val LOGIN_ID_HEADER = "X-Loopers-LoginId"
        private const val LOGIN_PW_HEADER = "X-Loopers-LoginPw"
    }
}
