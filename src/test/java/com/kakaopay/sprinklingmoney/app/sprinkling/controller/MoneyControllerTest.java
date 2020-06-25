package com.kakaopay.sprinklingmoney.app.sprinkling.controller;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;

import com.kakaopay.sprinklingmoney.app.common.utils.JsonUtils;
import com.kakaopay.sprinklingmoney.app.controller.sprinkling.SprinklingMoneyRequest;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoneyControllerTest {

//	@Test
//	public void makeRequestModel() {
//
//		final SprinklingMoneyRequest sprinklingMoneyModel = SprinklingMoneyRequest.create(
//			SprinklingMoney.builder()
//				.userId("fd151207-83b6-4014-84de-517e2c1a025a")
//				.messageRoomId("3a87933c-ea2a-4667-8cd2-dcd076c07eb4")
//				.receiverCount(5)
//				.amount(10000L)
//				.build()
//		);
//
//		final String json = JsonUtils.toJson(sprinklingMoneyModel);
//		log.info("#### {}", json);
//	}

	/**
	 * test userId : fd151207-83b6-4014-84de-517e2c1a025a
	 * test roomId : 3a87933c-ea2a-4667-8cd2-dcd076c07eb4
	 */
	@Ignore
	@Test
	public void makeUserIdAndRoomId() {
		log.info("userId : {}", UUID.randomUUID());
		log.info("roomId : {}", UUID.randomUUID());
	}

	/**
	 * testUser : fd151207-83b6-4014-84de-517e2c1a025a
	 * roomMembers :
	 * 		cea55869-f8bb-4b7b-b142-40c073d124a5
	 * 		5510eae0-cf4c-4488-a6e4-07e25ad030e0
	 * 		d61b76f0-5708-4ca6-8e20-c2897dbea1c8
	 * 		129ae99d-c21f-4020-bfa1-a2a71a4a1e8a
	 */
	@Ignore
	@Test
	public void makeUserList() {
		IntStream.range(1, 5)
			.mapToObj(i -> UUID.randomUUID())
			.peek(uuid -> log.info("userId : {}", uuid))
			.collect(Collectors.toList());
	}



}