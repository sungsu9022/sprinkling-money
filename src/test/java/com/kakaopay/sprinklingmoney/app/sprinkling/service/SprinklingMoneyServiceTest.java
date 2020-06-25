package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.messageroom.MessageRoom;
import com.kakaopay.sprinklingmoney.app.messageroom.MessageRoomService;
import com.kakaopay.sprinklingmoney.app.receive.service.SprinklingMoneyReceiveService;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;
import com.kakaopay.sprinklingmoney.app.sprinkling.repository.SprinklingMoneyRepository;
import com.kakaopay.sprinklingmoney.app.user.User;

import lombok.extern.slf4j.Slf4j;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class SprinklingMoneyServiceTest {
	@InjectMocks
	private SprinklingMoneyService sprinklingMoneyService;

	@Mock
	private SprinklingMoneyRepository sprinklingMoneyRepository;
	@Mock
	private MessageRoomService messageRoomService;
	@Mock
	private SprinklingMoneyReceiveService receiveService;
	@Mock
	private SprinklingMoneyDivider sprinklingMoneyDivider;
	@Mock
	private SprinklingMoneyTokenGenerator tokenGenerator;

	@Test(expected = SprinklingMoneyException.class)
	public void getSprinklingMoney_not_exist() {
		final String token = "abc", userId = "test", roomId = "Test";
		Mockito.when(sprinklingMoneyRepository.findByTokenAndUserIdAndMessageRoomId(anyString(), anyString(), anyString())).thenReturn(Optional.empty());
		sprinklingMoneyService.getSprinklingMoney(token, userId, roomId);
	}

	@Test
	public void getSprinklingMoney_success() {
		final String token = "abc", userId = "test", roomId = "Test";
		final SprinklingMoney expected = makeSprinklingMoney(userId, roomId);

		Mockito.when(sprinklingMoneyRepository.findByTokenAndUserIdAndMessageRoomId(anyString(), anyString(), anyString())).thenReturn(Optional.of(expected));

		final SprinklingMoney sprinklingMoney = sprinklingMoneyService.getSprinklingMoney(token, userId, roomId);
		assertTrue(expected.equals(sprinklingMoney));
	}

	@Test
	public void createSprinklingMoney() {
		final String userId = "fd151207-83b6-4014-84de-517e2c1a025a", roomId = "Test";
		final String expected = "abc";

		Mockito.when(messageRoomService.getMessageRoom(anyString())).thenReturn(MessageRoom.dummyMessageRoom(roomId));
		Mockito.when(tokenGenerator.makeToken(any())).thenReturn(expected);

		final String token = sprinklingMoneyService.createSprinklingMoney(makeSprinklingMoney(userId, roomId));
		assertTrue(expected.equals(token));
	}

	@Test
	public void getOthersInMessageRoom() {
		final String userId = "fd151207-83b6-4014-84de-517e2c1a025a", roomId = "Test";
		final List<User> othersInMessageRoom = sprinklingMoneyService.getOthersInMessageRoom(makeSprinklingMoney(userId, roomId), MessageRoom.dummyMessageRoom(roomId));
		final boolean isExistsMySelf = othersInMessageRoom.stream()
			.filter(u -> StringUtils.equals(u.getId(), userId))
			.findAny()
			.isPresent();

		assertFalse(isExistsMySelf);
	}

	private SprinklingMoney makeSprinklingMoney(String userId, String roomId) {
		return SprinklingMoney.builder()
			.userId(userId)
			.messageRoomId(roomId)
			.receiverCount(5)
			.amount(10000L)
			.build();
	}

}