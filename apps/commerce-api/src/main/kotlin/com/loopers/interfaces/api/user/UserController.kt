package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.interfaces.api.ApiResponse
import com.loopers.interfaces.api.user.request.SignUpRequest
import com.loopers.interfaces.api.user.response.SignUpResponse
import org.springframework.http.HttpStatus
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
        @RequestBody request: SignUpRequest,
    ): ApiResponse<SignUpResponse> {
        return userFacade.signUp(request.toCriteria())
            .let { SignUpResponse.from(it) }
            .let { ApiResponse.success(it) }
    }
}
