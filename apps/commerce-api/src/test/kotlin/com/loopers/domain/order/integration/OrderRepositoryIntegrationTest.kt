package com.loopers.domain.order.integration

import com.loopers.domain.order.infrastructure.persistence.OrderItemJpaRepository
import com.loopers.domain.order.port.OrderRepository
import com.loopers.domain.order.support.OrderSteps.Companion.주문항목_도메인_생성
import com.loopers.domain.order.support.OrderSteps.Companion.주문_도메인_생성
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OrderRepositoryIntegrationTest
    @Autowired
    constructor(
        private val orderRepository: OrderRepository,
        private val orderItemJpaRepository: OrderItemJpaRepository,
        private val databaseCleanUp: DatabaseCleanUp,
    ) {
        @AfterEach
        fun tearDown() {
            databaseCleanUp.truncateAllTables()
        }

        @Test
        fun `주문과_주문항목을_저장한다`() {
            val order = 주문_도메인_생성(
                id = 0L,
                items = listOf(
                    주문항목_도메인_생성(productId = 10L, quantity = 2, unitPrice = 10_000),
                    주문항목_도메인_생성(productId = 20L, quantity = 1, unitPrice = 5_000),
                ),
            )

            val saved = orderRepository.save(order)

            assertThat(saved.id).isPositive()
            assertThat(saved.paymentPrice.value).isEqualTo(25_000)
            assertThat(saved.items).hasSize(2)
            assertThat(saved.items).allMatch { it.orderId == saved.id }
            assertThat(orderItemJpaRepository.findByIdOrderId(saved.id)).hasSize(2)
        }
    }
