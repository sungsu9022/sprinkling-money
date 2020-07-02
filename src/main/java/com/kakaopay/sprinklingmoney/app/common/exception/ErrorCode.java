package com.kakaopay.sprinklingmoney.app.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode {
	MISSING_PARAMETER("4001", HttpStatus.BAD_REQUEST)
	, ILLEGAL_ACCESS("4002", HttpStatus.FORBIDDEN)
	, NOT_EXIST("5000", HttpStatus.INTERNAL_SERVER_ERROR)
	, EXPIRED("5001", HttpStatus.INTERNAL_SERVER_ERROR)
	, NOT_TARGET("5002", HttpStatus.INTERNAL_SERVER_ERROR)
	, ALREADY_DONE("5003", HttpStatus.INTERNAL_SERVER_ERROR)
	, DATA_VALIDATION_ERROR("5004", HttpStatus.INTERNAL_SERVER_ERROR)
	, TIMEOUT("5005", HttpStatus.INTERNAL_SERVER_ERROR)
	, UNKNOWN_ERROR("9999", HttpStatus.INTERNAL_SERVER_ERROR)

	;

	private String code;
	private HttpStatus httpStatus;
}
