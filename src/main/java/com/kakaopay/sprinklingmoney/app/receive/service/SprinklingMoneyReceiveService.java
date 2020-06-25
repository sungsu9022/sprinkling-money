package com.kakaopay.sprinklingmoney.app.receive.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.receive.repository.SprinklingMoneyReceiveRepository;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;
import com.kakaopay.sprinklingmoney.app.sprinkling.repository.SprinklingMoneyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprinklingMoneyReceiveService {
	private final SprinklingMoneyReceiveRepository receiveRepository;
	private final SprinklingMoneyRepository moneyRepository;

	/**
	 * 페이머니 받기
	 * @param token
	 * @param userId
	 * @param roomId
	 * @return
	 */
	public SprinklingMoneyReceive receive(String token, String userId, String roomId) {
		final SprinklingMoney money = moneyRepository.findByTokenAndMessageRoomId(token, roomId)
			.orElseThrow(() -> SprinklingMoneyException.builder()
				.errorCode(ErrorCode.NOT_EXIST)
				.message("존재하지 않는 페이 머니 뿌리기 정보입니다.")
				.build()
			);

		if(money.isExpired()) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.EXPIRED)
				.message("이미 만료된 페이 머니 뿌리기입니다.")
				.build();
		}

		final SprinklingMoneyReceive moneyReceive = receiveRepository.findByUserIdAndSprinklingMoneyNo(userId, money.getNo())
			.orElseThrow(() -> SprinklingMoneyException.builder()
				.errorCode(ErrorCode.NOT_TARGET)
				.message("페이 머니 지급 대상이 아닙니다.")
				.build()
			);

		if(moneyReceive.isAlreadyReceive()) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.ALREADY_DONE)
				.message("이미 받기가 완료되었습니다.")
				.build();
		}

		moneyReceive.setReceiveDate(LocalDateTime.now());
		return receiveRepository.save(moneyReceive);
	}


	/**
	 * sprinklingMoneyNo에 맵핑되는 Receive 목록 조회
	 * @param sprinklingMoneyNo
	 * @return
	 */
	public List<SprinklingMoneyReceive> getSprinklingMoneyReceiveList(int sprinklingMoneyNo) {
		return receiveRepository.findAllBySprinklingMoneyNo(sprinklingMoneyNo);
	}

	/**
	 * 뿌리진 페이머니 대상 목록 저장
	 * @param list
	 * @return
	 */
	@Transactional
	public List<SprinklingMoneyReceive> saveSprinklingMoneyList(List<SprinklingMoneyReceive> list) {
		return receiveRepository.saveAll(list);
	}
}
