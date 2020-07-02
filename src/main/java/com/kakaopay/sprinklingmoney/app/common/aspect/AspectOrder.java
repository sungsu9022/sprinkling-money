package com.kakaopay.sprinklingmoney.app.common.aspect;

import org.springframework.transaction.annotation.Transactional;

import com.kakaopay.sprinklingmoney.app.lock.RedisLock;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum  AspectOrder {
	REDIS_LOCK(0, RedisLock.class),
	TRANSACTIONAL(1, Transactional.class)
	;

	private int order;
	private Class clazz;
}
