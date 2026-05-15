package com.loopers.domain.user.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByLoginId(loginId: String): UserJpaEntity?
    fun existsByLoginId(loginId: String): Boolean
}
