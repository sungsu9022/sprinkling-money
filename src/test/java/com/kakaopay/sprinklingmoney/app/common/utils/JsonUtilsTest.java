package com.kakaopay.sprinklingmoney.app.common.utils;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtilsTest {

	@Test
	public void toJsonTest() {
		final String expected = "{\"test\":1}";
		final String json = JsonUtils.toJson(ImmutableMap.builder()
			.put("test", 1)
			.build());

		log.info("json : {}", json);
		assertTrue(StringUtils.equals(expected, json));
	}

	@Test
	public void toObjectTest() {
		final Map<String, Object> expected = ImmutableMap.<String,Object>builder()
			.put("test", 1)
			.build();

		final Map<String, Object> map = JsonUtils.toObject("{\"test\":1}", Map.class);

		log.info("map : {}", map);
		assertTrue(expected.equals(map));
	}

}