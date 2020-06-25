package com.kakaopay.sprinklingmoney.app.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public final class JsonUtils {

	private static final ObjectMapper DEFAULT_OBJECT_MAPPER;
	static {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new ParameterNamesModule());
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JavaTimeModule());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		DEFAULT_OBJECT_MAPPER = mapper;
	}

	public static String toJson(Object object) {
		try {
			return DEFAULT_OBJECT_MAPPER.writeValueAsString(object);
		} catch (Exception e) {
			log.error("[object to json failure] object : {}", object);
			return null;
		}
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		T object = null;
		try {
			object = DEFAULT_OBJECT_MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			log.error(String.format("[json to object failure] json : %s, class : %s", json, clazz), e);
		}
		return object;
	}
}
