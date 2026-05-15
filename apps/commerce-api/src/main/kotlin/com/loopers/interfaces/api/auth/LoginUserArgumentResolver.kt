package com.loopers.interfaces.api.auth

import com.loopers.application.user.UserFacade
import com.loopers.application.user.UserInfo
import com.loopers.interfaces.api.CurrentUserInfoRequestHeaders
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginUserArgumentResolver(
    private val userFacade: UserFacade,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(LoginUser::class.java) &&
            parameter.parameterType == UserInfo::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java) ?: throwUnauthorized()
        val loginId = request.getHeader(CurrentUserInfoRequestHeaders.LOGIN_ID) ?: throwUnauthorized()
        val rawPassword = request.getHeader(CurrentUserInfoRequestHeaders.LOGIN_PW) ?: throwUnauthorized()
        return userFacade.getMe(loginId, rawPassword)
    }

    private fun throwUnauthorized(): Nothing {
        throw CoreException(ErrorType.UNAUTHORIZED)
    }
}
