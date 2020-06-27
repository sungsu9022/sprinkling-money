package com.kakaopay.sprinklingmoney.app.receive.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

@Service
class RandomMoneyGenerator {
	static final int MAX_RATIO = 100;
	static final int MAX_RECEIVER_COUNT = 99;
	static final int RANDOM_MONEY_MAX_LOOP_COUNT = 5;

	/**
	 *
	 * @param sprinklingMoney
	 * @return
	 */
	public DividedMoney create(SprinklingMoney sprinklingMoney, String userId) {
		final int currentReceiverCount = (int) sprinklingMoney.getReceiveList().stream().count();
		final long currentReceivedMoney = sprinklingMoney.getReceiveList().stream().mapToLong(SprinklingMoneyReceive::getMoney).sum();

		final int ratio = determinateRatio(sprinklingMoney.getReceiverCount() - currentReceiverCount);
		final long money = determinateMoney(sprinklingMoney.getAmount(), currentReceivedMoney, ratio);

		return DividedMoney.builder()
			.userId(userId)
			.amount(money)
			.build();
	}

	long createMoney(SprinklingMoney sprinklingMoney) {
		final int currentReceiverCount = (int) sprinklingMoney.getReceiveList().stream().count();
		final long currentReceivedMoney = sprinklingMoney.getReceiveList().stream().mapToLong(SprinklingMoneyReceive::getMoney).sum();

		if(sprinklingMoney.getAmount() == currentReceivedMoney) {
			return 0;
		}

		if(isLastReceiver(sprinklingMoney)) {
			return sprinklingMoney.getAmount() - currentReceivedMoney;
		}

		long result;
		int loopCount =0;
		while (true) {
			loopCount++;
			int randomRatio = determinateRatio(sprinklingMoney.getReceiverCount() - currentReceiverCount);
			result = determinateMoney(sprinklingMoney.getAmount(), currentReceivedMoney, randomRatio);
			if(RANDOM_MONEY_MAX_LOOP_COUNT < loopCount && sprinklingMoney.getAmount() < sprinklingMoney.getReceiverCount()) {
				break;
			}

			if(result < 1) {
				continue;
			}

			break;
		}

		return result;

	}

	boolean isLastReceiver(SprinklingMoney sprinklingMoney) {
		return (sprinklingMoney.getReceiverCount() == sprinklingMoney.getReceiveList().stream().count() + 1);
	}

	/**
	 *
	 * @param totalAmount
	 * @param ratio
	 * @return
	 */
	long determinateMoney(long totalAmount, long currentReceivedMoney, int ratio) {
		final long money = (long) Math.floor(totalAmount * 0.01 * ratio);

		if(totalAmount < currentReceivedMoney + money) {
			return 0;
		}

		return money;
	}


	/**
	 * 분배될 확률 결정
	 * @param restReceiverCount
	 * @return
	 */
	int determinateRatio(int restReceiverCount) {
		if(restReceiverCount >= MAX_RECEIVER_COUNT) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.DATA_VALIDATION_ERROR)
				.message(String.format("%s명 이상으로는 뿌리기를 수행할 수 없습니다.", MAX_RECEIVER_COUNT + 1))
				.build();
		}

		final Random random = new Random(System.currentTimeMillis());

		int ratio = 0;
		int currentRestRatio = MAX_RATIO - restReceiverCount;
		while(true) {
			// 비율 추첨이 완료되면 반복문 종료
			if(ratio > 0) {
				break;
			}

			final int randomRatio = random.nextInt(currentRestRatio);
			// 0%인 경우 재추첨
			if(randomRatio == 0) {
				continue;
			}

			// 모든 인원이 최소 1%라도 가져갈수 있도록 (멤버의비율 + 남은인원)보다 큰 경우 재추첨
			if(MAX_RATIO <= (randomRatio + restReceiverCount)) {
				continue;
			}

			ratio = randomRatio;
		}

		return ratio;
	}
}
