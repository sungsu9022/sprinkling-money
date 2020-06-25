package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SprinklingMoneyTokenGeneratorTest {
	private SprinklingMoneyTokenGenerator generator = new SprinklingMoneyTokenGenerator();

	@Test
	public void makeTokenTest() {
		final ImmutableMap<Object, Object> source = ImmutableMap.builder()
			.put("test", "test")
			.put("test2", "test2")
			.put("test3", "test3")
			.put("test4", "test4")
			.build();

		final String token = generator.makeToken(source);
		log.info("token : {}", token);
		assertTrue(token.length() == SprinklingMoneyTokenGenerator.TOKEN_LENGTH);

	}

}