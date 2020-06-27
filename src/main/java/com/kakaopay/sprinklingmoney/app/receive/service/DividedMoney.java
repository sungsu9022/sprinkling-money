package com.kakaopay.sprinklingmoney.app.receive.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
class DividedMoney {
	private String userId;
	private long amount;
}
