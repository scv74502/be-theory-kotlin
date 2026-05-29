package com.loopers.domain.order.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemJpaRepository : JpaRepository<OrderItemJpaEntity, OrderItemJpaId> {
    fun findByIdOrderId(orderId: Long): List<OrderItemJpaEntity>
}
