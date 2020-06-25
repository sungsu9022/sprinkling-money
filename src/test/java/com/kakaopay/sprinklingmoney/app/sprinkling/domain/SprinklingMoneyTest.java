package com.kakaopay.sprinklingmoney.app.sprinkling.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.common.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SprinklingMoneyTest {

	@Test
	public void createSprinklingMoneyForRequest() {
		final SprinklingMoney model = SprinklingMoney.builder()
			.userId("fd151207-83b6-4014-84de-517e2c1a025a")
			.messageRoomId("3a87933c-ea2a-4667-8cd2-dcd076c07eb4")
			.receiverCount(5)
			.amount(10000L)
			.build();

		final String json = JsonUtils.toJson(model);
		log.info("#### {}", json);
		assertTrue(model != null);
	}

	@Test(expected = SprinklingMoneyException.class)
	public void createSprinklingMoneyException() {
		SprinklingMoney.builder()
			.userId("fd151207-83b6-4014-84de-517e2c1a025a")
			.receiverCount(5)
			.amount(10000L)
			.build();
	}
}