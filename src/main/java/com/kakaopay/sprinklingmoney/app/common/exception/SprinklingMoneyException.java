package com.kakaopay.sprinklingmoney.app.common.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SprinklingMoneyException extends RuntimeException {
	private String message;
	private ErrorCode errorCode;

	@Builder
	private SprinklingMoneyException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.message = message;
	}
}
