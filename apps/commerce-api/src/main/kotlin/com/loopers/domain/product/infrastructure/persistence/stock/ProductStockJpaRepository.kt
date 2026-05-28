package com.loopers.domain.product.infrastructure.persistence.stock

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductStockJpaRepository : JpaRepository<ProductStockJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ProductStockJpaEntity s where s.productId in :productIds")
    fun findByProductIdsForUpdate(
        @Param("productIds") productIds: Set<Long>,
    ): List<ProductStockJpaEntity>
}
