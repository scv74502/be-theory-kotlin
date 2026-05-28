package com.loopers.domain.product.vo

import com.loopers.domain.product.exception.InvalidProductException

class ProductName private constructor(
    val value: String,
) {
    companion object {
        fun of(value: String): ProductName {
            if (value.isBlank()) {
                throw InvalidProductException("상품명은 공백일 수 없습니다.")
            }
            return ProductName(value)
        }
    }
}
