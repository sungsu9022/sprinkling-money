package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import com.kakaopay.sprinklingmoney.app.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
class DividedMoney {
	private User member;
	private long amount;

	public void addAmount(long amount) {
		this.amount += amount;
	}
}
