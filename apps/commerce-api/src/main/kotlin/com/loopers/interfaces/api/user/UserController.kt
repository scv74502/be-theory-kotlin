package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.application.user.UserInfo
import com.loopers.interfaces.api.ApiResponse
import com.loopers.interfaces.api.auth.LoginUser
import com.loopers.interfaces.api.user.request.SignUpRequest
import com.loopers.interfaces.api.user.response.MyUserResponse
import com.loopers.interfaces.api.user.response.SignUpResponse
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
