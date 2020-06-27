package com.kakaopay.sprinklingmoney.app.receive.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RandomMoneyGeneratorTest {
	private RandomMoneyGenerator generator = new RandomMoneyGenerator();


	@Test(expected = SprinklingMoneyException.class)
	public void determinateRatio_max_receiver_count() {
		generator.determinateRatio(100);
	}

	@Test
	public void determinateRatio() {
		IntStream.range(0, 100)
			.map(i -> generator.determinateRatio(50))
			.forEach(ratio -> log.info("ratio : {}", ratio));
	}

	@Test
	public void determinateMoney_failure() {
		final long money = generator.determinateMoney(100, 70, 35);
		assertTrue(money == 0);
	}

	@Test
	public void determinateMoney_success() {
		int totalAmount = 100, currentReceivedMoney = 40, ratio = 35;
		final long money = generator.determinateMoney(totalAmount, currentReceivedMoney, ratio);
		log.info("money : {}", money);
		assertTrue(money == (long) Math.floor(100 * 0.01 * 35));
	}

	@Test
	public void createMoney_single() {
		final long totalAmount = 10000;
		int receiverCount = 5;
		final SprinklingMoney money = makeSprinklingMoney(totalAmount, receiverCount);
		final long createdMoney = generator.createMoney(money);
		log.info("createdMoney : {}", createdMoney);
		assertTrue(createdMoney > 0 && createdMoney < money.getAmount());
	}

	@Test
	public void createMoney_total() {
		// internalCreateMoneyTotal(10000, 5);
		internalCreateMoneyTotal(1, 5);
		internalCreateMoneyTotal(500000, 5);
		internalCreateMoneyTotal(5523434, 6);
		internalCreateMoneyTotal(2312412, 9);
	}

	private void internalCreateMoneyTotal(long totalAmount, int receiverCount) {
		final SprinklingMoney money = makeSprinklingMoney(totalAmount, receiverCount);
		final List<SprinklingMoneyReceive> receiveList = money.getReceiveList();

		for(int i=0;i<money.getReceiverCount();i++) {
			final long createdMoney = generator.createMoney(money);
			log.info("createdMoney : {}", createdMoney);
			receiveList.add(SprinklingMoneyReceive.create(money, "testUserId", createdMoney));
		}

		final long sum = receiveList.stream().map(SprinklingMoneyReceive::getMoney).mapToLong(Long::longValue).sum();
		log.info("moneyTotalAmount : {}, sum : {}", money.getAmount(), sum);
		assertTrue(money.getAmount() == sum);
	}


	private SprinklingMoney makeSprinklingMoney(long totalAmount, int receiverCount) {
		return SprinklingMoney.builder()
			.userId("testId")
			.messageRoomId("testRoomId")
			.receiverCount(receiverCount)
			.amount(totalAmount)
			.build();
	}
}