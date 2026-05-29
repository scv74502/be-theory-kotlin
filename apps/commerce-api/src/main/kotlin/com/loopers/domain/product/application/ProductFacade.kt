package com.loopers.domain.product.application

import com.loopers.domain.brand.application.service.BrandService
import com.loopers.domain.product.application.command.ProductRegisterCommand
import com.loopers.domain.product.application.command.ProductSearchCommand
import com.loopers.domain.product.application.command.ProductUpdateCommand
import com.loopers.domain.product.application.info.ProductDetailInfo
import com.loopers.domain.product.application.info.ProductInfo
import com.loopers.domain.product.application.info.ProductSummaryInfo
import com.loopers.domain.product.application.service.ProductService
import com.loopers.domain.product.application.service.StockService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductFacade(
    private val brandService: BrandService,
    private val productService: ProductService,
    private val stockService: StockService,
) {
    @Transactional
    fun registerProduct(command: ProductRegisterCommand): ProductInfo {
        brandService.findById(command.brandId)
        val product = productService.register(command)
        stockService.initialize(
            productId = product.id,
            leftStock = command.initialStock,
        )
        return ProductInfo.from(product)
    }

    fun updateProduct(
        productId: Long,
        command: ProductUpdateCommand,
    ): ProductInfo = productService.update(productId, command).let { ProductInfo.from(it) }

    fun deleteProduct(productId: Long) {
        productService.softDelete(productId)
    }

    fun getProduct(productId: Long): ProductDetailInfo {
        val product = productService.findById(productId)
        val brand = brandService.findById(product.brandId)
        return ProductDetailInfo.from(product, brand)
    }

    fun findProducts(command: ProductSearchCommand): List<ProductSummaryInfo> =
        productService.findProducts(command).map { ProductSummaryInfo.from(it) }
}
