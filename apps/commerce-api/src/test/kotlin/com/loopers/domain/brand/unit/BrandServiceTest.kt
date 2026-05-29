package com.loopers.domain.brand.unit

import com.loopers.domain.brand.model.BrandModel
import com.loopers.domain.brand.port.BrandRepository
import com.loopers.domain.brand.application.service.BrandService
import com.loopers.domain.brand.support.BrandSteps.Companion.기본_브랜드_ID
import com.loopers.domain.brand.support.BrandSteps.Companion.브랜드_도메인_생성
import com.loopers.domain.brand.support.BrandSteps.Companion.브랜드_등록_커맨드
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class BrandServiceTest {
    @Test
    fun `브랜드를_등록한다`() {
        val brandRepository = mockk<BrandRepository>()
        val brandService = BrandService(brandRepository)
        val brandSlot = slot<BrandModel>()
        every { brandRepository.save(capture(brandSlot)) } answers {
            brandSlot.captured.copy(id = 기본_브랜드_ID)
        }

        val brand = brandService.register(브랜드_등록_커맨드())

        assertThat(brand.id).isEqualTo(기본_브랜드_ID)
        assertThat(brandSlot.captured.name.value).isEqualTo("기본 브랜드")
    }

    @Test
    fun `삭제된_브랜드_조회는_NOT_FOUND가_발생한다`() {
        val brandRepository = mockk<BrandRepository>()
        val brandService = BrandService(brandRepository)
        every { brandRepository.findById(기본_브랜드_ID) } returns 브랜드_도메인_생성(
            id = 기본_브랜드_ID,
            deletedAtOrNull = ZonedDateTime.now(),
        )

        val ex = assertThrows<CoreException> {
            brandService.findById(기본_브랜드_ID)
        }

        assertThat(ex.errorType).isEqualTo(ErrorType.NOT_FOUND)
    }
}
