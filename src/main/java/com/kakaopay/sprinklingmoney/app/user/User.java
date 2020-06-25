package com.kakaopay.sprinklingmoney.app.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
	private String id;

	public static List<User> createDummyUserList() {
		return Arrays.asList(
			"fd151207-83b6-4014-84de-517e2c1a025a"
			, "cea55869-f8bb-4b7b-b142-40c073d124a5"
			, "5510eae0-cf4c-4488-a6e4-07e25ad030e0"
			, "d61b76f0-5708-4ca6-8e20-c2897dbea1c8"
			, "129ae99d-c21f-4020-bfa1-a2a71a4a1e8a")
			.stream()
			.map(User::new)
			.collect(Collectors.toList());
	}
}
