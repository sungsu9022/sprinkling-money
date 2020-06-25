package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.user.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SprinklingMoneyDividerTest {
	private SprinklingMoneyDivider divider = new SprinklingMoneyDivider();

	@Test
	public void getDividedMoney() {
		long totalAmount = 10000;
		final List<DividedMoney> dividedMoney = divider.makeDividedMoneyList(User.createDummyUserList(), totalAmount, 5);

		final long actualSum = dividedMoney
			.stream()
			.mapToLong(DividedMoney::getAmount)
			.sum();

		log.info("totalAmount : {}, actualSum : {}", actualSum, totalAmount);
		assertTrue(totalAmount == actualSum);
	}

	@Test
	public void divideMoneyListTest() {
		internalDivideMoneyList(5, 1);
		internalDivideMoneyList(5, 500000);
		internalDivideMoneyList(6, 5523434);
		internalDivideMoneyList(9, 2312412);
	}

	private void internalDivideMoneyList(int receiverCount, long totalAmount) {
		final List<Integer> ratioList = divider.divideRatioList(receiverCount);
		final List<Long> dividedMoneyList = divider.divideMoneyList(totalAmount, ratioList);

		final long sumOfDivided = dividedMoneyList.stream().mapToLong(Long::longValue).sum();
		log.info("dividedMoneyList : {}, sumOfDivided : {}", dividedMoneyList, sumOfDivided);
		assertTrue(sumOfDivided == totalAmount);
	}


	@Test
	public void divideRatioListTest() {
		final List<Integer> ratioList = divider.divideRatioList(5);
//		ratioList.stream().forEach(System.out::println);
		assertTrue(ratioList.stream().mapToInt(Integer::intValue).sum() == SprinklingMoneyDivider.MAX_RATIO);

		final List<Integer> ratioList2 = divider.divideRatioList(50);
		ratioList2.stream().forEach(System.out::println);
		assertTrue(ratioList2.stream().mapToInt(Integer::intValue).sum() == SprinklingMoneyDivider.MAX_RATIO);

		final List<Integer> ratioList3 = divider.divideRatioList(80);
//		ratioList3.stream().forEach(System.out::println);
		assertTrue(ratioList3.stream().mapToInt(Integer::intValue).sum() == SprinklingMoneyDivider.MAX_RATIO);
	}

	@Test(expected = SprinklingMoneyException.class)
	public void divideRatioListTest_data_validation_error() {
		final List<Integer> ratioList = divider.divideRatioList(100);
		ratioList.stream().forEach(System.out::println);
		assertTrue(ratioList.stream().mapToInt(Integer::intValue).sum() == SprinklingMoneyDivider.MAX_RATIO);
	}

}