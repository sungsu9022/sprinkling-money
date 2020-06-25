/*
 * @(#)GlobalExceptionHandler.java $version 2014. 4. 1.
 *
 * Copyright 2014 Naver Corp. All rights Reserved.
 * Naver PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.kakaopay.sprinklingmoney.app.common.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.common.models.ErrorResponse;
import com.kakaopay.sprinklingmoney.app.common.models.ResponseModel;

import lombok.extern.slf4j.Slf4j;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * 기본 예외 핸들링
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(SprinklingMoneyException.class)
	public ResponseEntity<ResponseModel> handleSprinklingMoneyException(SprinklingMoneyException e, HttpServletRequest request) {
		final ErrorCode errorCode = e.getErrorCode();
		log.error("Error : {}, Message : {}", e);
		return new ResponseEntity<>(new ErrorResponse(errorCode.getCode(), e.getMessage()), errorCode.getHttpStatus());
	}

	/**
	 * 처리되지 않은 예외를 핸들링
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponse handleException(Exception e, HttpServletRequest request) {
		log.error("Error : {}, Message : {}", e);
		ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
		return new ErrorResponse(errorCode.getCode(), "Unknown Error");
	}
}
