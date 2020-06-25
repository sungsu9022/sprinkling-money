package com.kakaopay.sprinklingmoney.app.controller.sprinkling;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SprinklingMoneyRequest {
	private int receiverCount;
	private long amount;
}
