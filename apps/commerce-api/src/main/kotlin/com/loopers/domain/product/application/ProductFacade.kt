package com.loopers.domain.product.application

import com.loopers.domain.product.application.command.ProductRegisterCommand
import com.loopers.domain.product.application.command.ProductSearchCommand
import com.loopers.domain.product.application.command.ProductUpdateCommand
import com.loopers.domain.product.application.info.ProductInfo
import com.loopers.domain.product.application.service.ProductService
import org.springframework.stereotype.Component

@Component
class ProductFacade(
    private val productService: ProductService,
) {
    fun registerProduct(command: ProductRegisterCommand): ProductInfo =
        productService.register(command).let { ProductInfo.from(it) }

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
