package com.loopers.domain.product.presentation.response

import com.loopers.domain.product.application.info.ProductInfo

data class ProductResponse(
    val id: Long,
    val brandId: Long,
    val name: String,
    val price: Long,
) {
    companion object {
        fun from(info: ProductInfo): ProductResponse = ProductResponse(
            id = info.id,
            brandId = info.brandId,
            name = info.name,
            price = info.price,
        )
    }
}
