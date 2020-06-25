package com.kakaopay.sprinklingmoney.app.receive.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.common.utils.JsonUtils;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.receive.repository.SprinklingMoneyReceiveRepository;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;
import com.kakaopay.sprinklingmoney.app.sprinkling.repository.SprinklingMoneyRepository;

import lombok.extern.slf4j.Slf4j;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class SprinklingMoneyReceiveServiceTest {

	@InjectMocks
	private SprinklingMoneyReceiveService sprinklingMoneyReceiveService;

	@Mock
	private SprinklingMoneyReceiveRepository receiveRepository;
	@Mock
	private SprinklingMoneyRepository moneyRepository;


	@Test(expected = SprinklingMoneyException.class)
	public void receiveTest_not_exists_money() {
		final String token = "abc", userId = "test", roomId = "Test";
		Mockito.when(moneyRepository.findByTokenAndMessageRoomId(anyString(), anyString())).thenReturn(Optional.empty());
		sprinklingMoneyReceiveService.receive(token, userId, roomId);
	}

	@Test(expected = SprinklingMoneyException.class)
	public void receiveTest_expired() {
		final String token = "abc", userId = "test", roomId = "Test";
		Mockito.when(moneyRepository.findByTokenAndMessageRoomId(anyString(), anyString())).thenReturn(Optional.of(makeExpiredMoney()));
		sprinklingMoneyReceiveService.receive(token, userId, roomId);
	}

	@Test(expected = SprinklingMoneyException.class)
	public void receiveTest_not_target() {
		final String token = "abc", userId = "test", roomId = "Test";
		Mockito.when(moneyRepository.findByTokenAndMessageRoomId(anyString(), anyString())).thenReturn(
			Optional.of(SprinklingMoney.builder()
				.userId(userId)
				.messageRoomId(roomId)
				.amount(10000L)
				.receiverCount(5)
				.build()
			)
		);
		Mockito.when(receiveRepository.findByUserIdAndSprinklingMoneyNo(anyString(), anyInt())).thenReturn(Optional.empty());

		sprinklingMoneyReceiveService.receive(token, userId, roomId);
	}



	@Test(expected = SprinklingMoneyException.class)
	public void receiveTest_already_done() {
		final String token = "abc", userId = "test", roomId = "Test";
		final SprinklingMoney sprinklingMoney = SprinklingMoney.builder()
			.userId(userId)
			.messageRoomId(roomId)
			.amount(10000L)
			.receiverCount(5)
			.build();
		final SprinklingMoneyReceive receive = SprinklingMoneyReceive.create(sprinklingMoney, "fd151207-83b6-4014-84de-517e2c1a025a", 5000);
		receive.setReceiveDate(LocalDateTime.now());

		Mockito.when(moneyRepository.findByTokenAndMessageRoomId(anyString(), anyString())).thenReturn(Optional.of(sprinklingMoney));
		Mockito.when(receiveRepository.findByUserIdAndSprinklingMoneyNo(anyString(), anyInt())).thenReturn(Optional.of(receive));

		sprinklingMoneyReceiveService.receive(token, userId, roomId);
	}

	@Test
	public void receiveTest_success() {
		final String token = "abc", userId = "test", roomId = "Test";
		final SprinklingMoney sprinklingMoney = SprinklingMoney.builder()
			.userId(userId)
			.messageRoomId(roomId)
			.amount(10000L)
			.receiverCount(5)
			.build();
		final SprinklingMoneyReceive receive = SprinklingMoneyReceive.create(sprinklingMoney, "fd151207-83b6-4014-84de-517e2c1a025a", 5000);
		final SprinklingMoneyReceive receiveCompleted = SprinklingMoneyReceive.create(sprinklingMoney, "fd151207-83b6-4014-84de-517e2c1a025a", 5000);
		receiveCompleted.setReceiveDate(LocalDateTime.now());

		Mockito.when(moneyRepository.findByTokenAndMessageRoomId(anyString(), anyString())).thenReturn(Optional.of(sprinklingMoney));
		Mockito.when(receiveRepository.findByUserIdAndSprinklingMoneyNo(anyString(), anyInt())).thenReturn(Optional.of(receive));
		Mockito.when(receiveRepository.save(any())).thenReturn(receiveCompleted);


		final SprinklingMoneyReceive saved = sprinklingMoneyReceiveService.receive(token, userId, roomId);
		assertTrue(Objects.nonNull(saved.getReceiveDate()));
	}

	private SprinklingMoney makeExpiredMoney() {
		return JsonUtils.toObject("{\"no\":0,\"userId\":\"fd151207-83b6-4014-84de-517e2c1a025a\",\"messageRoomId\":\"3a87933c-ea2a-4667-8cd2-dcd076c07eb4\",\"receiverCount\":5,\"amount\":10000,\"expireDate\":\"2020-06-27T04:43:57.612\",\"createdDate\":\"2020-06-27T04:33:57.612\"}", SprinklingMoney.class);
	}


}