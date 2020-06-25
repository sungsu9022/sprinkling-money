package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.kakaopay.sprinklingmoney.app.common.utils.JsonUtils;

@Service
class SprinklingMoneyTokenGenerator {
	static final int TOKEN_LENGTH = 3;

	/**
	 * 토큰 발행
	 * @param source
	 * @param <T>
	 * @return token
	 */
	public <T>String makeToken(T source) {
		String token = Hashing.sha256()
			.hashString(JsonUtils.toJson(source) + LocalDateTime.now(), StandardCharsets.UTF_8)
			.toString()
			.substring(0, TOKEN_LENGTH);

		return token;
	}

}
