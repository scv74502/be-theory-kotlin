package com.loopers.domain.product.unit

import com.loopers.domain.product.application.ProductFacade
import com.loopers.domain.product.application.command.ProductSearchCommand
import com.loopers.domain.product.application.info.ProductDetailInfo
import com.loopers.domain.product.application.info.ProductSummaryInfo
import com.loopers.domain.product.application.service.ProductService
import com.loopers.domain.product.application.service.StockService
import com.loopers.domain.product.support.ProductSteps.Companion.기본_상품_ID
import com.loopers.domain.product.support.ProductSteps.Companion.재고_도메인_생성
import com.loopers.domain.product.support.ProductSteps.Companion.상품_도메인_생성
import com.loopers.domain.product.support.ProductSteps.Companion.상품_등록_커맨드
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductFacadeTest {
    @Test
    fun `상품_등록_결과를_Info로_반환한다`() {
        val productService = mockk<ProductService>()
        val stockService = mockk<StockService>()
        val productFacade = ProductFacade(productService, stockService)
        every { productService.register(상품_등록_커맨드()) } returns 상품_도메인_생성(id = 기본_상품_ID)
        every { stockService.initialize(기본_상품_ID, 10L) } returns 재고_도메인_생성(productId = 기본_상품_ID)

        val info = productFacade.registerProduct(상품_등록_커맨드())

        assertThat(info.id).isEqualTo(기본_상품_ID)
        assertThat(info.name).isEqualTo("기본 상품")
        assertThat(info.price).isEqualTo(10_000)
    }

    @Test
    fun `상품_등록_시_초기_재고를_함께_생성한다`() {
        val productService = mockk<ProductService>()
        val stockService = mockk<StockService>()
        val productFacade = ProductFacade(productService, stockService)
        val command = 상품_등록_커맨드(initialStock = 15)
        every { productService.register(command) } returns 상품_도메인_생성(id = 기본_상품_ID)
        every { stockService.initialize(기본_상품_ID, 15) } returns 재고_도메인_생성(
            productId = 기본_상품_ID,
            leftStock = 15,
        )

        productFacade.registerProduct(command)

        verifySequence {
            productService.register(command)
            stockService.initialize(기본_상품_ID, 15)
        }
    }

    @Test
    fun `상품_상세_결과를_Info로_반환한다`() {
        val productService = mockk<ProductService>()
        val stockService = mockk<StockService>()
        val productFacade = ProductFacade(productService, stockService)
        every { productService.findById(기본_상품_ID) } returns 상품_도메인_생성(id = 기본_상품_ID)

        val info = productFacade.getProduct(기본_상품_ID)

        assertThat(info).isInstanceOf(ProductDetailInfo::class.java)
        assertThat(info.id).isEqualTo(기본_상품_ID)
        assertThat(info.brandId).isEqualTo(10L)
    }

    @Test
    fun `상품_목록_결과를_Info_목록으로_반환한다`() {
        val productService = mockk<ProductService>()
        val stockService = mockk<StockService>()
        val productFacade = ProductFacade(productService, stockService)
        val command = ProductSearchCommand(brandId = 10L)
        every { productService.findProducts(command) } returns listOf(
            상품_도메인_생성(id = 2L),
            상품_도메인_생성(id = 1L),
        )

        val infos = productFacade.findProducts(command)

        assertThat(infos.first()).isInstanceOf(ProductSummaryInfo::class.java)
        assertThat(infos.map { it.id }).containsExactly(2L, 1L)
    }
}
