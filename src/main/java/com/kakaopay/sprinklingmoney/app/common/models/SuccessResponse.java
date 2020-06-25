package com.kakaopay.sprinklingmoney.app.common.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SuccessResponse<T> implements ResponseModel {
	private final T result;
}
