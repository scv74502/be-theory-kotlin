package com.loopers.domain.product.vo

import com.loopers.domain.product.exception.InvalidProductException

class Quantity private constructor(
    val value: Long,
) {
    companion object {
        fun of(value: Long): Quantity {
            if (value <= 0) {
                throw InvalidProductException("수량은 1개 이상이어야 합니다.")
            }
            return Quantity(value)
        }
    }
}
