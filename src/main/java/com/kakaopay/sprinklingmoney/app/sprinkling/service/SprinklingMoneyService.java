package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;
import com.kakaopay.sprinklingmoney.app.sprinkling.repository.SprinklingMoneyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprinklingMoneyService {
	private final SprinklingMoneyRepository sprinklingMoneyRepository;
	private final SprinklingMoneyTokenGenerator tokenGenerator;

	/**
	 * 뿌려진 페이머니 조회
	 *
	 * @param token
	 * @param userId
	 * @param roomId
	 * @return
	 */
	public SprinklingMoney getSprinklingMoney(String token, String userId, String roomId) {
		return sprinklingMoneyRepository.findByTokenAndUserIdAndMessageRoomId(token, userId, roomId)
			.orElseThrow(() -> SprinklingMoneyException.builder()
				.errorCode(ErrorCode.NOT_EXIST)
				.message("존재하지 않는 페이 머니 뿌리기입니다.")
				.build()
			);
	}
	/**
	 *뿌려진 페이머니 조회
	 * @param token
	 * @param roomId
	 * @return
	 */
	public SprinklingMoney getSprinklingMoney(String token, String roomId) {
		return sprinklingMoneyRepository.findByTokenAndMessageRoomId(token, roomId)
			.orElseThrow(() -> SprinklingMoneyException.builder()
				.errorCode(ErrorCode.NOT_EXIST)
				.message("존재하지 않는 페이 머니 뿌리기입니다.")
				.build()
			);
	}

	/**
	 * 페이머니 뿌리기 추가
	 * @param sprinklingMoney
	 * @return
	 */
	@Transactional
	public String createSprinklingMoney(SprinklingMoney sprinklingMoney) {
		final String token = tokenGenerator.makeToken(sprinklingMoney);
		sprinklingMoney.setToken(token);

		final SprinklingMoney saved = sprinklingMoneyRepository.save(sprinklingMoney);
		return saved.getToken();
	}
}
