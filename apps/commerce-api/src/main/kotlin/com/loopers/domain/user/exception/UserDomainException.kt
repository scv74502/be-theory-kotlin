package com.loopers.domain.user.exception

open class UserDomainException(
    message: String,
) : RuntimeException(message)

class InvalidUserException(
    message: String,
) : UserDomainException(message)

class InvalidPasswordException(
    message: String,
) : UserDomainException(message)
