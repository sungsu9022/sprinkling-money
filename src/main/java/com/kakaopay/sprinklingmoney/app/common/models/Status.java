package com.kakaopay.sprinklingmoney.app.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
	SUCCESS(200)
	;

	int code;

}
