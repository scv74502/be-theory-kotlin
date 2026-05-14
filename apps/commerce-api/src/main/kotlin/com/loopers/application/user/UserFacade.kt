package com.loopers.application.user

import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userService: UserService,
) {
    fun signUp(criteria: UserCriteria.SignUp): UserInfo {
        return userService.signUp(criteria.toCommand())
            .let { UserInfo.from(it) }
    }
}
