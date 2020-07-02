package com.kakaopay.sprinklingmoney.app.lock;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
class TimeoutExecutorService {
	private static final Executor executor = Executors.newFixedThreadPool(10);

	public CompletableFuture<Void> timeout(long timeout, TimeUnit unit) {
		return CompletableFuture.runAsync(() -> {
			log.info("timeout thread sleep start : {}", LocalDateTime.now());
			try {
				Thread.sleep(unit.toMillis(timeout));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("TASK completed");
		}, executor)
		.thenAccept(p -> {
			log.info("timeout thread sleep end : {}", LocalDateTime.now());
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.UNKNOWN_ERROR)
				.message("lock timeout")
				.build();
		});
	}
}
