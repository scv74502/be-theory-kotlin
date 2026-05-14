package com.loopers.domain.user

import com.loopers.domain.user.vo.Password
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signUp(command: UserCommand.SignUp): UserModel {
        if (userRepository.existsByLoginId(command.loginId)) {
            throw CoreException(ErrorType.CONFLICT, "이미 가입된 로그인 ID 입니다.")
        }
        val password = Password.of(command.rawPassword, command.birthday, passwordEncoder)
        val user = UserModel(
            loginId = command.loginId,
            password = password,
            name = command.name,
            birthday = command.birthday,
            email = command.email,
        )
        return userRepository.save(user)
    }
}
