package com.loopers.domain.user

class DuplicateLoginIdException(
    val loginId: String,
) : RuntimeException("Duplicate login id: $loginId")
