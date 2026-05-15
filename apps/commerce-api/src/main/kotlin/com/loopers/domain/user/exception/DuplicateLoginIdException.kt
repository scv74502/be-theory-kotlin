package com.loopers.domain.user.exception

class DuplicateLoginIdException(
    val loginId: String,
) : RuntimeException("Duplicate login id: $loginId")
