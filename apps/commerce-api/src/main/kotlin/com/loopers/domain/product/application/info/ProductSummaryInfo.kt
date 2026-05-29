package com.loopers.domain.product.application.info

import com.loopers.domain.product.model.ProductModel

data class ProductSummaryInfo(
    val id: Long,
    val brandId: Long,
    val name: String,
    val price: Long,
) {
    companion object {
        fun from(product: ProductModel): ProductSummaryInfo = ProductSummaryInfo(
            id = product.id,
            brandId = product.brandId,
            name = product.name.value,
            price = product.price.value,
        )
    }
}
