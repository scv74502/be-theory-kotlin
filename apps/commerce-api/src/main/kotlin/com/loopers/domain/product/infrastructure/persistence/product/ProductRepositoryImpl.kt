package com.loopers.domain.product.infrastructure.persistence.product

import com.loopers.domain.product.model.ProductModel
import com.loopers.domain.product.port.ProductRepository
import org.springframework.stereotype.Component

@Component
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
) : ProductRepository {
    override fun save(product: ProductModel): ProductModel {
        val entity = if (product.id == 0L) {
            ProductJpaEntity.fromDomain(product)
        } else {
            productJpaRepository.findById(product.id).orElseThrow()
                .also { it.updateFrom(product) }
        }
        return productJpaRepository.saveAndFlush(entity).toDomain()
    }

    override fun saveAll(products: List<ProductModel>): List<ProductModel> = products.map { save(it) }

    override fun findById(productId: Long): ProductModel? =
        productJpaRepository.findById(productId).map { it.toDomain() }.orElse(null)

    override fun findAllByIds(productIds: Collection<Long>): List<ProductModel> =
        productJpaRepository.findAllByIds(productIds).map { it.toDomain() }

    override fun findByBrandId(brandId: Long): List<ProductModel> =
        productJpaRepository.findByBrandId(brandId).map { it.toDomain() }

    override fun findLatest(brandId: Long?): List<ProductModel> =
        productJpaRepository.findLatest(brandId).map { it.toDomain() }
}
