package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.user.User;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
class SprinklingMoneyDivider {
	static final int MAX_RATIO = 100;


	/**
	 *
	 * @param sprinkling
	 * @param othersInMessageRoom
	 * @return
	 */
	public List<DividedMoney> divide(SprinklingMoney sprinkling, List<User> othersInMessageRoom) {
		if(othersInMessageRoom.size() != sprinkling.getReceiverCount()) {
			throw SprinklingMoneyException.builder()
				.message("방 인원과 뿌리는 인원이 맞지 않습니다.")
				.errorCode(ErrorCode.DATA_VALIDATION_ERROR)
				.build();
		}

		return makeDividedMoneyList(othersInMessageRoom, sprinkling.getAmount(), sprinkling.getReceiverCount());


	}

	/**
	 *
	 * @param othersInMessageRoom
	 * @param totalAmount
	 * @param receiverCount
	 * @return
	 */
	List<DividedMoney> makeDividedMoneyList(List<User> othersInMessageRoom, long totalAmount, int receiverCount) {
		final List<Integer> ratioList = divideRatioList(receiverCount);
		final List<Long> divideMoneyList = divideMoneyList(totalAmount, ratioList);

		return IntStream.range(0, divideMoneyList.size())
			.mapToObj(index -> DividedMoney.builder()
					.member(othersInMessageRoom.get(index))
					.amount(divideMoneyList.get(index))
					.build()
			)
			.collect(Collectors.toList());
	}

	/**
	 *
	 * @param totalAmount
	 * @param ratioList
	 * @return
	 */
	List<Long> divideMoneyList(long totalAmount, List<Integer> ratioList) {
		final List<Long> dividedMoneyList = ratioList.stream()
			.map(ratio -> (long)Math.floor(totalAmount * 0.01 * ratio))
			.collect(Collectors.toList());

		final long sumOfDivided = dividedMoneyList.stream().mapToLong(Long::longValue).sum();
		if(sumOfDivided != totalAmount) {
			final long difference = totalAmount - sumOfDivided;
			final int lastItemIndex = dividedMoneyList.size() - 1;
			final Long lastItemMoney = dividedMoneyList.get(lastItemIndex);

			dividedMoneyList.remove(lastItemIndex);
			dividedMoneyList.add(lastItemMoney + difference);
		}

		return dividedMoneyList;
	}


	/**
	 *
	 * @param receiverCount
	 * @return
	 */
	List<Integer> divideRatioList(int receiverCount) {
		final List<Integer> ratioList = new ArrayList<>();
		final Random random = new Random(System.currentTimeMillis());

		int currentRestRatio = MAX_RATIO;
		int currentReceiverCount = receiverCount;
		while(true) {
			// n-1 명 추첨이 완료되면 반복문 종료
			if(ratioList.size() == receiverCount - 1) {
				break;
			}

			final int randomRatio = random.nextInt(currentRestRatio);
			// 0%인 경우 재추첨
			if(randomRatio == 0) {
				continue;
			}

			// 모든 인원이 최소 1%라도 가져갈수 있도록 (멤버의비율 + 남은인원)보다 큰 경우 재추첨
			if(currentRestRatio <= (randomRatio + currentReceiverCount)) {
				continue;
			}

			ratioList.add(randomRatio);
			currentReceiverCount -=1;
			currentRestRatio -= randomRatio;
		}

		ratioList.add(currentRestRatio);
		Collections.shuffle(ratioList);
		return ratioList;
	}
}
