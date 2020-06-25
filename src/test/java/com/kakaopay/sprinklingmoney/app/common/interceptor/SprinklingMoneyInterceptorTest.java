package com.kakaopay.sprinklingmoney.app.common.interceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.messageroom.MessageRoomService;
import com.kakaopay.sprinklingmoney.app.user.UserService;

@RunWith(MockitoJUnitRunner.class)
public class SprinklingMoneyInterceptorTest {
	@InjectMocks
	private SprinklingMoneyInterceptor interceptor;

	@Mock
	private UserService userService;
	@Mock
	private MessageRoomService messageRoomService;


	@Test(expected = SprinklingMoneyException.class)
	public void preHandleTestWithException() throws Exception {
		Mockito.when(userService.isValidUser(anyString())).thenReturn(false);
		interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), any());
	}

	@Test(expected = SprinklingMoneyException.class)
	public void preHandleTestWithException2() throws Exception {
		Mockito.when(userService.isValidUser(anyString())).thenReturn(true);
		Mockito.when(messageRoomService.isValidMessageRoom(anyString())).thenReturn(false);
		interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), any());
	}
}