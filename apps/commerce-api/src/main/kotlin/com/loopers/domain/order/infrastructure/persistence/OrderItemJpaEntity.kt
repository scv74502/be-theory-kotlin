package com.loopers.domain.order.infrastructure.persistence

import com.loopers.domain.order.model.OrderItemModel
import com.loopers.domain.product.vo.Money
import com.loopers.domain.product.vo.Quantity
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "order_items")
class OrderItemJpaEntity(
    @EmbeddedId
    var id: OrderItemJpaId,
    @Column(nullable = false)
    var quantity: Long,
    @Column(name = "snapshot_product_name", nullable = false)
    var snapshotProductName: String,
    @Column(name = "snapshot_unit_price", nullable = false)
    var snapshotUnitPrice: Long,
    @Column(name = "line_price", nullable = false)
    var linePrice: Long,
) {
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: ZonedDateTime
        protected set

    @PrePersist
    private fun prePersist() {
        createdAt = ZonedDateTime.now()
    }

    fun toDomain(): OrderItemModel = OrderItemModel.snapshotOf(
        orderId = id.orderId,
        productId = id.productId,
        quantity = Quantity.of(quantity),
        snapshotProductName = snapshotProductName,
        snapshotUnitPrice = Money.of(snapshotUnitPrice),
    )

    companion object {
        fun fromDomain(item: OrderItemModel): OrderItemJpaEntity = OrderItemJpaEntity(
            id = OrderItemJpaId(
                orderId = item.orderId,
                productId = item.productId,
            ),
            quantity = item.quantity.value,
            snapshotProductName = item.snapshotProductName,
            snapshotUnitPrice = item.snapshotUnitPrice.value,
            linePrice = item.linePrice.value,
        )
    }
}
