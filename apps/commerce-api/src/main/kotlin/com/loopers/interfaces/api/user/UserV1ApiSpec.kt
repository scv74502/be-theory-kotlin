package com.loopers.interfaces.api.user

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "User V1 API", description = "Loopers 사용자 API 입니다.")
interface UserV1ApiSpec {
    @Operation(
        summary = "회원 가입",
        description = "신규 회원을 등록합니다.",
    )
    fun signUp(request: UserV1Dto.SignUpRequest): ApiResponse<UserV1Dto.SignUpResponse>
}
