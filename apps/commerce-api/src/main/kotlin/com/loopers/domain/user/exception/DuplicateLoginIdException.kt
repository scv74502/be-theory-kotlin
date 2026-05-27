package com.loopers.domain.user.exception

class DuplicateLoginIdException(
    loginId: String,
) : UserDomainException("이미 가입된 로그인 ID 입니다.")
