package com.loopers.domain.like.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LikeJpaRepository : JpaRepository<LikeJpaEntity, LikeJpaId> {
    fun existsByIdUserIdAndIdProductId(
        userId: Long,
        productId: Long,
    ): Boolean

    fun countByIdProductId(productId: Long): Long

    @Query(
        """
        select l.id.productId as productId, count(l) as likeCount
        from LikeJpaEntity l
        where l.id.productId in :productIds
        group by l.id.productId
        """,
    )
    fun countByProductIds(
        @Param("productIds") productIds: Set<Long>,
    ): List<ProductLikeCountRow>
}

interface ProductLikeCountRow {
    fun getProductId(): Long
    fun getLikeCount(): Long
}
