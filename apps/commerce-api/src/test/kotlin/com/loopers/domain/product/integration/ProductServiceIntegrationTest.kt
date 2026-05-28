package com.loopers.domain.product.integration

import com.loopers.domain.product.application.service.ProductService
import com.loopers.domain.product.application.command.ProductSearchCommand
import com.loopers.domain.product.infrastructure.persistence.product.ProductJpaRepository
import com.loopers.domain.product.support.ProductSteps.Companion.상품_등록_커맨드
import com.loopers.domain.product.support.ProductSteps.Companion.상품_수정_커맨드
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
class ProductServiceIntegrationTest
    @Autowired
    constructor(
        private val productService: ProductService,
        private val productJpaRepository: ProductJpaRepository,
        private val databaseCleanUp: DatabaseCleanUp,
    ) {
        @AfterEach
        fun tearDown() {
            databaseCleanUp.truncateAllTables()
        }

        @Test
        fun `유효한_커맨드면_상품이_저장된다`() {
            val product = productService.register(상품_등록_커맨드())

            val saved = productJpaRepository.findById(product.id).orElseThrow()
            assertThat(saved.brandId).isEqualTo(10L)
            assertThat(saved.productName).isEqualTo("기본 상품")
            assertThat(saved.price).isEqualTo(10_000)
        }

        @Test
        fun `상품을_수정하면_변경된_값이_저장된다`() {
            val product = productService.register(상품_등록_커맨드())

            productService.update(
                productId = product.id,
                command = 상품_수정_커맨드(name = "수정 상품", price = 20_000),
            )

            val saved = productJpaRepository.findById(product.id).orElseThrow()
            assertThat(saved.productName).isEqualTo("수정 상품")
            assertThat(saved.price).isEqualTo(20_000)
        }

        @Test
        fun `상품을_삭제하면_조회가_불가하다`() {
            val product = productService.register(상품_등록_커맨드())

            productService.softDelete(product.id)

            val ex = assertThrows<CoreException> {
                productService.findById(product.id)
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.NOT_FOUND)
        }

        @Test
        fun `상품_목록은_삭제된_상품을_제외하고_최신순으로_조회한다`() {
            val first = productService.register(상품_등록_커맨드(name = "첫 상품", price = 1_000))
            val second = productService.register(상품_등록_커맨드(name = "둘째 상품", price = 2_000))
            val deleted = productService.register(상품_등록_커맨드(name = "삭제 상품", price = 3_000))
            productService.softDelete(deleted.id)

            val products = productService.findProducts(ProductSearchCommand(brandId = 10L))

            assertThat(products.map { it.id }).containsExactly(second.id, first.id)
        }
    }
