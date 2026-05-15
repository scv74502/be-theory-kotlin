package com.loopers.domain.user.application

import com.loopers.domain.user.application.command.UserSignUpCommand
import com.loopers.domain.user.exception.DuplicateLoginIdException
import com.loopers.domain.user.exception.UserDomainException
import com.loopers.domain.user.model.UserModel
import com.loopers.domain.user.port.PasswordEncoder
import com.loopers.domain.user.port.UserRepository
import com.loopers.domain.user.vo.Birthday
import com.loopers.domain.user.vo.Email
import com.loopers.domain.user.vo.LoginId
import com.loopers.domain.user.vo.Name
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
    fun signUp(command: UserSignUpCommand): UserModel {
        return try {
            if (userRepository.existsByLoginId(command.loginId)) {
                throwDuplicateLoginIdConflict()
            }
            val birthday = Birthday.of(command.birthday)
            val password = Password.of(command.rawPassword, birthday, passwordEncoder)
            val user = UserModel(
                loginId = LoginId.of(command.loginId),
                password = password,
                name = Name.of(command.name),
                birthday = birthday,
                email = Email.of(command.email),
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

    @Transactional(readOnly = true)
    fun getMe(loginId: String, rawPassword: String): UserModel {
        val user = userRepository.findByLoginId(loginId) ?: throwUnauthorized()
        if (!passwordEncoder.matches(rawPassword, user.password.encoded)) {
            throwUnauthorized()
        }
        return user
    }

    private fun throwDuplicateLoginIdConflict(): Nothing {
        throw CoreException(ErrorType.CONFLICT, DUPLICATE_LOGIN_ID_MESSAGE)
    }

    private fun throwUnauthorized(): Nothing {
        throw CoreException(ErrorType.UNAUTHORIZED)
    }

    companion object {
        private const val DUPLICATE_LOGIN_ID_MESSAGE = "이미 가입된 로그인 ID 입니다."
    }
}
