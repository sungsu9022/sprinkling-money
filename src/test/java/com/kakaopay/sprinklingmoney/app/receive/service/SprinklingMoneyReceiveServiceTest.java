package com.kakaopay.sprinklingmoney.app.receive.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

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
	public void receiveTest_expired() {
		final String moneyUserId = "test", userId = "test2", roomId = "Test";
		final SprinklingMoney money = makeExpiredMoney();
		sprinklingMoneyReceiveService.receive(money, userId);
	}

	@Test(expected = SprinklingMoneyException.class)
	public void receiveTest_already_done() {
		final String moneyUserId = "test", userId = "test2", roomId = "Test";
		final SprinklingMoney money = makeSprinklingMoney(moneyUserId, roomId, 5);
		money.setReceiveList(Arrays.asList(SprinklingMoneyReceive.create(money, userId, 300)));
		sprinklingMoneyReceiveService.receive(money, userId);
	}

	@Test(expected = SprinklingMoneyException.class)
	public void receiveTest_not_exist() {
		final String moneyUserId = "test", userId = "test2", roomId = "Test";
		final SprinklingMoney money = makeSprinklingMoney(moneyUserId, roomId, 2);
		money.setReceiveList(
			Arrays.asList(
				SprinklingMoneyReceive.create(money, "test3", 300)
				, SprinklingMoneyReceive.create(money, "test4", 300)
			)
		);

		sprinklingMoneyReceiveService.receive(money, userId);
	}

	@Test
	public void receiveTest_success() {
		final String moneyUserId = "test", userId = "test2", roomId = "Test";
		final SprinklingMoney money = makeSprinklingMoney(moneyUserId, roomId, 3);
		money.setReceiveList(
			Arrays.asList(
				SprinklingMoneyReceive.create(money, "test3", 300)
				, SprinklingMoneyReceive.create(money, "test4", 300)
			)
		);
		final SprinklingMoneyReceive receiveCompleted = SprinklingMoneyReceive.create(money, "fd151207-83b6-4014-84de-517e2c1a025a", 5000);
		receiveCompleted.setReceiveDate(LocalDateTime.now());

		Mockito.when(receiveRepository.save(any())).thenReturn(receiveCompleted);


		final SprinklingMoneyReceive saved = sprinklingMoneyReceiveService.receive(money, userId);
		assertTrue(Objects.nonNull(saved.getReceiveDate()));
	}

	private SprinklingMoney makeExpiredMoney() {
		return JsonUtils.toObject("{\"no\":0,\"userId\":\"fd151207-83b6-4014-84de-517e2c1a025a\",\"messageRoomId\":\"3a87933c-ea2a-4667-8cd2-dcd076c07eb4\",\"receiverCount\":5,\"amount\":10000,\"expireDate\":\"2020-06-27T04:43:57.612\",\"createdDate\":\"2020-06-27T04:33:57.612\"}", SprinklingMoney.class);
	}

	private SprinklingMoney makeSprinklingMoney(String userId, String roomId, int receiverCount) {
		final SprinklingMoney money = SprinklingMoney.builder()
			.userId(userId)
			.messageRoomId(roomId)
			.receiverCount(receiverCount)
			.amount(10000L)
			.build();
		return money;
	}


}