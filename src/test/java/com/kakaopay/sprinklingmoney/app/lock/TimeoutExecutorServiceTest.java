package com.kakaopay.sprinklingmoney.app.lock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeoutExecutorServiceTest {

	TimeoutExecutorService service = new TimeoutExecutorService();

	@Test
	public void timeoutCompletedExceptionally() throws InterruptedException {
		log.info("main thread start");
		final CompletableFuture<Void> timeout = service.timeout(10, TimeUnit.MILLISECONDS);
		Thread.sleep(1000);
		final boolean timeoutCompletedExceptionally = timeout.isCompletedExceptionally();
		if(timeoutCompletedExceptionally) {
			log.info("timeout exception");
		}
		log.info("main thread end");

		assertTrue(timeoutCompletedExceptionally);
	}

	@Test
	public void timeout() throws InterruptedException {
		log.info("main thread start");
		final CompletableFuture<Void> timeout = service.timeout(2000, TimeUnit.MILLISECONDS);
		Thread.sleep(1000);
		final boolean timeoutCompletedExceptionally = timeout.isCompletedExceptionally();
		if(timeoutCompletedExceptionally) {
			log.info("timeout exception");
		}
		log.info("main thread end");

		assertFalse(timeoutCompletedExceptionally);
	}


}