package com.loopers.domain.product.integration

import com.loopers.domain.product.application.ProductFacade
import com.loopers.domain.product.infrastructure.persistence.product.ProductJpaRepository
import com.loopers.domain.product.infrastructure.persistence.stock.ProductStockJpaRepository
import com.loopers.domain.product.support.ProductSteps.Companion.상품_등록_커맨드
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
class ProductFacadeIntegrationTest
    @Autowired
    constructor(
        private val productFacade: ProductFacade,
        private val productJpaRepository: ProductJpaRepository,
        private val productStockJpaRepository: ProductStockJpaRepository,
        private val databaseCleanUp: DatabaseCleanUp,
    ) {
        @AfterEach
        fun tearDown() {
            databaseCleanUp.truncateAllTables()
        }

        @Test
        fun `상품_등록은_초기_재고를_함께_저장한다`() {
            val product = productFacade.registerProduct(상품_등록_커맨드(initialStock = 15))

            val savedProduct = productJpaRepository.findById(product.id)
            val savedStock = productStockJpaRepository.findById(product.id).orElseThrow()
            assertThat(savedProduct).isPresent
            assertThat(savedStock.leftStock).isEqualTo(15)
        }

        @Test
        fun `초기_재고가_유효하지_않으면_상품과_재고가_저장되지_않는다`() {
            val ex = assertThrows<CoreException> {
                productFacade.registerProduct(상품_등록_커맨드(initialStock = -1))
            }

            assertThat(ex.errorType).isEqualTo(ErrorType.BAD_REQUEST)
            assertThat(productJpaRepository.count()).isZero()
            assertThat(productStockJpaRepository.count()).isZero()
        }
    }
