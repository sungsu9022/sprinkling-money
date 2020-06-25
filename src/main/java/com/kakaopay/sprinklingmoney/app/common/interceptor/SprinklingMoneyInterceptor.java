package com.kakaopay.sprinklingmoney.app.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.common.resolver.MessageRoomIdArgumentResolver;
import com.kakaopay.sprinklingmoney.app.common.resolver.UserIdArgumentResolver;
import com.kakaopay.sprinklingmoney.app.messageroom.MessageRoomService;
import com.kakaopay.sprinklingmoney.app.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SprinklingMoneyInterceptor extends HandlerInterceptorAdapter {
	private final UserService userService;
	private final MessageRoomService messageRoomService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		final String userId = request.getHeader(UserIdArgumentResolver.USER_ID_HEADER);
		final String roomId = request.getHeader(MessageRoomIdArgumentResolver.ROOM_ID_HEADER);

		log.info("requestUri : {}", request.getRequestURI());

		if(!userService.isValidUser(userId)) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.ILLEGAL_ACCESS)
				.message("유효하지 않은 유저입니다.")
				.build();
		}

		if(!messageRoomService.isValidMessageRoom(roomId)) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.ILLEGAL_ACCESS)
				.message("유효하지 않은 메시지방입니다.")
				.build();
		}

		return super.preHandle(request, response, handler);
	}
}
