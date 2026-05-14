package com.loopers.domain.user

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signUp(command: UserCommand.SignUp): UserModel =
        TODO("Green 단계에서 중복 검사 -> 비밀번호 정책 검증/인코딩 -> 저장 흐름 구현")
}
