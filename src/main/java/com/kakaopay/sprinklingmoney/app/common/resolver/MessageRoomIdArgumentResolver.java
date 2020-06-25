package com.kakaopay.sprinklingmoney.app.common.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MessageRoomIdArgumentResolver implements HandlerMethodArgumentResolver {
	public static final String ROOM_ID_HEADER = "X-ROOM-ID";

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return String.class.isAssignableFrom(methodParameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		final HttpServletRequest request = (HttpServletRequest)nativeWebRequest.getNativeRequest();
		final String roomId = request.getHeader(ROOM_ID_HEADER);

		log.info("roomId : {}", roomId);
		return roomId;
	}
}
