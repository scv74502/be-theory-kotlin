package com.loopers.domain.product.vo

import com.loopers.domain.product.exception.InvalidProductException

class Money private constructor(
    val value: Long,
) {
    companion object {
        fun of(value: Long): Money {
            if (value < 0) {
                throw InvalidProductException("가격은 음수일 수 없습니다.")
            }
            return Money(value)
        }
    }
}
