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
        return try {
            if (userRepository.existsByLoginId(command.loginId)) {
                throwDuplicateLoginIdConflict()
            }
            val password = Password.of(command.rawPassword, command.birthday, passwordEncoder)
            val user = UserModel(
                loginId = command.loginId,
                password = password,
                name = command.name,
                birthday = command.birthday,
                email = command.email,
            )
            try {
                userRepository.save(user)
            } catch (_: DuplicateLoginIdException) {
                throwDuplicateLoginIdConflict()
            }
        } catch (e: UserDomainException) {
            throw CoreException(ErrorType.BAD_REQUEST, e.message)
        }
    }

    private fun throwDuplicateLoginIdConflict(): Nothing {
        throw CoreException(ErrorType.CONFLICT, DUPLICATE_LOGIN_ID_MESSAGE)
    }

    companion object {
        private const val DUPLICATE_LOGIN_ID_MESSAGE = "이미 가입된 로그인 ID 입니다."
    }
}
