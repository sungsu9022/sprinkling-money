package com.kakaopay.sprinklingmoney.app.receive.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.lock.RedisLock;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.receive.repository.SprinklingMoneyReceiveRepository;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprinklingMoneyReceiveService {
	private final SprinklingMoneyReceiveRepository receiveRepository;
	private final RandomMoneyGenerator generator;

	/**
	 * 페이머니 받기
	 * @param money
	 * @param userId
	 * @return
	 * @throws SprinklingMoneyException
	 */
	@Transactional
	@RedisLock(prefixKey = "sprinklingMoney", key = "#money.no")
	public SprinklingMoneyReceive receive(SprinklingMoney money, String userId) throws SprinklingMoneyException {
		if(money.isExpired()) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.EXPIRED)
				.message("이미 만료된 페이 머니 뿌리기입니다.")
				.build();
		}

		money.getReceiveList()
			.stream()
			.filter(r -> StringUtils.equals(r.getUserId(), userId))
			.findAny()
			.ifPresent(moneyReceive -> {
				throw SprinklingMoneyException.builder()
					.errorCode(ErrorCode.ALREADY_DONE)
					.message("이미 받기가 완료되었습니다.")
					.build();
			});

		if(money.getReceiveList().stream().count() == money.getReceiverCount()) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.NOT_EXIST)
				.message("더이상 받을 수 없습니다.")
				.build();
		}

		DividedMoney dividedMoney = generator.create(money, userId);
		final SprinklingMoneyReceive receive = SprinklingMoneyReceive.create(money, userId, dividedMoney.getAmount());
		receive.setReceiveDate(LocalDateTime.now());
		return receiveRepository.save(receive);
	}
}
