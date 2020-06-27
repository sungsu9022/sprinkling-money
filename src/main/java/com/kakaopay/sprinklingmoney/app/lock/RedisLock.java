package com.kakaopay.sprinklingmoney.app.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisLock {
	/**
	 * 어떠한 key 로 lock 을 걸지 정하는 표현식
	 * @return
	 */
	String key() default "";
}
