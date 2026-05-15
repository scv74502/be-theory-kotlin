package com.loopers.domain.user.presentation

import com.loopers.domain.user.application.UserFacade
import com.loopers.domain.user.application.UserInfo
import com.loopers.domain.user.presentation.auth.LoginUser
import com.loopers.domain.user.presentation.request.SignUpRequest
import com.loopers.domain.user.presentation.response.MyUserResponse
import com.loopers.domain.user.presentation.response.SignUpResponse
import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
        @Parameter(hidden = true) @LoginUser user: UserInfo,
    ): ApiResponse<MyUserResponse> {
        return MyUserResponse.from(user)
            .let { ApiResponse.success(it) }
    }
}
