package com.loopers.interfaces.api.user

import com.loopers.interfaces.api.ApiResponse
import com.loopers.interfaces.api.user.request.SignUpRequest
import com.loopers.interfaces.api.user.response.SignUpResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "User API", description = "Loopers 사용자 API 입니다.")
interface UserApiSpec {
    @Operation(
        summary = "회원 가입",
        description = "신규 회원을 등록합니다.",
    )
    fun signUp(request: SignUpRequest): ApiResponse<SignUpResponse>
}
