package com.loopers.domain.user.port

import com.loopers.domain.user.model.UserModel

interface UserRepository {
    fun existsByLoginId(loginId: String): Boolean
    fun save(user: UserModel): UserModel
    fun findByLoginId(loginId: String): UserModel?
}
