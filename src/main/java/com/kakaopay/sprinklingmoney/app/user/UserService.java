package com.kakaopay.sprinklingmoney.app.user;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	public boolean isValidUser(String userId) {
		return Optional.ofNullable(userId)
			.filter(StringUtils::isNoneBlank)
			.map(id -> Boolean.TRUE)
			.orElse(Boolean.FALSE);
	}
}
