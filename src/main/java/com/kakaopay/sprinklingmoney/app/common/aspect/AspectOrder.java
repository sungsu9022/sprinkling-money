package com.kakaopay.sprinklingmoney.app.common.aspect;

import java.util.concurrent.locks.Lock;

import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum  AspectOrder {
	REDIS_LOCK(0, Lock.class),
	TRANSACTIONAL(1, Transactional.class)
	;

	private int order;
	private Class clazz;
}
