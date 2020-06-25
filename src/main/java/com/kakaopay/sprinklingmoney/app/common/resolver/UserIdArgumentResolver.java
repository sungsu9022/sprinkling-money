package com.kakaopay.sprinklingmoney.app.common.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.kakaopay.sprinklingmoney.app.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
	public static final String USER_ID_HEADER = "X-USER-ID";

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return User.class.isAssignableFrom(methodParameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest)nativeWebRequest.getNativeRequest();
		final String userId = request.getHeader(USER_ID_HEADER);

		log.info("userId : {}", userId);
		return new User(userId);
	}
}
