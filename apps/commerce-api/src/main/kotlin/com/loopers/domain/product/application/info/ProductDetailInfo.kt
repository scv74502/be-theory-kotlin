package com.loopers.domain.product.application.info

import com.loopers.domain.product.model.ProductModel

data class ProductDetailInfo(
    val id: Long,
    val brandId: Long,
    val name: String,
    val price: Long,
) {
    companion object {
        fun from(product: ProductModel): ProductDetailInfo = ProductDetailInfo(
            id = product.id,
            brandId = product.brandId,
            name = product.name.value,
            price = product.price.value,
        )
    }
}
