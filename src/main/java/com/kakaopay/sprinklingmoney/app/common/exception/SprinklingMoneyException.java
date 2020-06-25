package com.kakaopay.sprinklingmoney.app.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
