package com.loopers.infrastructure.user

import com.loopers.domain.user.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoderAdapter : PasswordEncoder {
    override fun encode(raw: String): String =
        TODO("Green 단계에서 spring-security-crypto 의 BCryptPasswordEncoder 위임 구현")
}
