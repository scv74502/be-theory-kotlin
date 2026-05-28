package com.loopers.domain.product.integration

import com.loopers.domain.product.application.service.StockService
import com.loopers.domain.product.infrastructure.persistence.stock.ProductStockJpaRepository
import com.loopers.domain.product.support.ProductSteps.Companion.재고_차감_커맨드
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StockServiceIntegrationTest
    @Autowired
    constructor(
        private val stockService: StockService,
        private val productStockJpaRepository: ProductStockJpaRepository,
        private val databaseCleanUp: DatabaseCleanUp,
    ) {
        @AfterEach
        fun tearDown() {
            databaseCleanUp.truncateAllTables()
        }

        @Test
        fun `초기_재고를_저장한다`() {
            stockService.initialize(productId = 1L, leftStock = 10)

            val saved = productStockJpaRepository.findById(1L).orElseThrow()
            assertThat(saved.leftStock).isEqualTo(10)
        }

        @Test
        fun `재고를_차감하면_변경된_재고가_저장된다`() {
            stockService.initialize(productId = 1L, leftStock = 10)

            stockService.decreaseAll(listOf(재고_차감_커맨드(productId = 1L, quantity = 3)))

            val saved = productStockJpaRepository.findById(1L).orElseThrow()
            assertThat(saved.leftStock).isEqualTo(7)
        }

        @Test
        fun `재고가_부족하면_CONFLICT가_발생하고_재고를_저장하지_않는다`() {
            stockService.initialize(productId = 1L, leftStock = 2)

            val ex = assertThrows<CoreException> {
                stockService.decreaseAll(listOf(재고_차감_커맨드(productId = 1L, quantity = 3)))
            }

            val saved = productStockJpaRepository.findById(1L).orElseThrow()
            assertThat(ex.errorType).isEqualTo(ErrorType.CONFLICT)
            assertThat(saved.leftStock).isEqualTo(2)
        }
    }
