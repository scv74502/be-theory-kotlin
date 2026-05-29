package com.loopers.domain.product.application

import com.loopers.domain.product.application.command.ProductRegisterCommand
import com.loopers.domain.product.application.command.ProductSearchCommand
import com.loopers.domain.product.application.command.ProductUpdateCommand
import com.loopers.domain.product.application.info.ProductInfo
import com.loopers.domain.product.application.service.ProductService
import com.loopers.domain.product.application.service.StockService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductFacade(
    private val productService: ProductService,
    private val stockService: StockService,
) {
    @Transactional
    fun registerProduct(command: ProductRegisterCommand): ProductInfo {
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

    fun getProduct(productId: Long): ProductInfo =
        productService.findById(productId).let { ProductInfo.from(it) }

    fun findProducts(command: ProductSearchCommand): List<ProductInfo> =
        productService.findProducts(command).map { ProductInfo.from(it) }
}
