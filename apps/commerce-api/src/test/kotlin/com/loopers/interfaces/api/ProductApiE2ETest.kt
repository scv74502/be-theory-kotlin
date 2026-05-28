package com.loopers.interfaces.api

import com.loopers.ApiTest
import com.loopers.domain.product.application.service.ProductService
import com.loopers.domain.product.presentation.response.ProductResponse
import com.loopers.domain.product.support.ProductSteps.Companion.상품_등록_커맨드
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ProductApiE2ETest
    @Autowired
    constructor(
        private val productService: ProductService,
    ) : ApiTest() {
        companion object {
            private const val ENDPOINT = "/api/v1/products"
        }

        private val productResponseType =
            object : ParameterizedTypeReference<ApiResponse<ProductResponse>>() {}
        private val productListResponseType =
            object : ParameterizedTypeReference<ApiResponse<List<ProductResponse>>>() {}

        @Test
        fun `존재하는_상품_ID면_상품_상세를_반환한다`() {
            val product = productService.register(상품_등록_커맨드())

            val response = testRestTemplate.exchange(
                "$ENDPOINT/${product.id}",
                HttpMethod.GET,
                HttpEntity<Any>(Unit),
                productResponseType,
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.data?.id).isEqualTo(product.id)
            assertThat(response.body?.data?.name).isEqualTo("기본 상품")
            assertThat(response.body?.data?.price).isEqualTo(10_000)
        }

        @Test
        fun `존재하지_않는_상품_ID면_404_NOT_FOUND를_반환한다`() {
            val response = testRestTemplate.exchange(
                "$ENDPOINT/999999",
                HttpMethod.GET,
                HttpEntity<Any>(Unit),
                productResponseType,
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        @Test
        fun `상품_목록은_브랜드로_필터링하고_최신순으로_반환한다`() {
            productService.register(상품_등록_커맨드(brandId = 20L, name = "다른 브랜드 상품", price = 1_000))
            val first = productService.register(상품_등록_커맨드(brandId = 10L, name = "첫 상품", price = 2_000))
            val second = productService.register(상품_등록_커맨드(brandId = 10L, name = "둘째 상품", price = 3_000))

            val response = testRestTemplate.exchange(
                "$ENDPOINT?brandId=10",
                HttpMethod.GET,
                HttpEntity<Any>(Unit),
                productListResponseType,
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.data?.map { it.id }).containsExactly(second.id, first.id)
        }

        @Test
        fun `삭제된_상품은_상세_조회에서_404_NOT_FOUND를_반환한다`() {
            val product = productService.register(상품_등록_커맨드())
            productService.softDelete(product.id)

            val response = testRestTemplate.exchange(
                "$ENDPOINT/${product.id}",
                HttpMethod.GET,
                HttpEntity<Any>(Unit),
                productResponseType,
            )

            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }
    }
