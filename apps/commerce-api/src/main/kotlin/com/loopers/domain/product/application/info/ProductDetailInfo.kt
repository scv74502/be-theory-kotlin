package com.loopers.domain.product.application.info

import com.loopers.domain.brand.model.BrandModel
import com.loopers.domain.product.model.ProductModel

data class ProductDetailInfo(
    val id: Long,
    val brandId: Long,
    val brandName: String,
    val name: String,
    val price: Long,
) {
    companion object {
        fun from(
            product: ProductModel,
            brand: BrandModel,
        ): ProductDetailInfo = ProductDetailInfo(
            id = product.id,
            brandId = product.brandId,
            brandName = brand.name.value,
            name = product.name.value,
            price = product.price.value,
        )
    }
}
