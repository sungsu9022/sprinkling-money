package com.kakaopay.sprinklingmoney.app.lock;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.kakaopay.sprinklingmoney.app.common.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {
	private static final String LOCK_KEY_NAME_PREFIX = "LOCK:";
	private static final long EXPIRED_MINUTES = 10;

	private final RedisTemplate<String, Object> redisTemplate;

	public <T> void setLock(String key, T value) {
		final ValueOperations<String, Object> vop = redisTemplate.opsForValue();
		final String redisLockKey = createKey(key);
		log.info("redisLockKey : {}", redisLockKey);
		vop.set(redisLockKey, JsonUtils.toJson(value), EXPIRED_MINUTES, TimeUnit.MINUTES);
	}

	public <T> void removeLock(String key) {
		final String redisLockKey = createKey(key);
		log.info("redisLockKey : {}", redisLockKey);
		redisTemplate.delete(redisLockKey);
	}

	public boolean isLocked(String key) {
		final ValueOperations<String, Object> vop = redisTemplate.opsForValue();
		final String redisLockKey = createKey(key);
		return StringUtils.isNotBlank((String) vop.get(redisLockKey));
	}

	String createKey(String key) {
		return LOCK_KEY_NAME_PREFIX + key;
	}

}
