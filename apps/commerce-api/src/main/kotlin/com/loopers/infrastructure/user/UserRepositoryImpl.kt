package com.loopers.infrastructure.user

import com.loopers.domain.user.DuplicateLoginIdException
import com.loopers.domain.user.UserModel
import com.loopers.domain.user.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun existsByLoginId(loginId: String): Boolean = userJpaRepository.existsByLoginId(loginId)

    override fun save(user: UserModel): UserModel =
        try {
            userJpaRepository.saveAndFlush(UserJpaEntity.fromDomain(user)).toDomain()
        } catch (_: DataIntegrityViolationException) {
            throw DuplicateLoginIdException(user.loginId)
        }

    override fun findByLoginId(loginId: String): UserModel? = userJpaRepository.findByLoginId(loginId)?.toDomain()
}
