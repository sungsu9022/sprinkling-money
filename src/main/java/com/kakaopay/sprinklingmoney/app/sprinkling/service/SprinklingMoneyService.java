package com.kakaopay.sprinklingmoney.app.sprinkling.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.messageroom.MessageRoom;
import com.kakaopay.sprinklingmoney.app.messageroom.MessageRoomService;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.receive.service.SprinklingMoneyReceiveService;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;
import com.kakaopay.sprinklingmoney.app.sprinkling.repository.SprinklingMoneyRepository;
import com.kakaopay.sprinklingmoney.app.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprinklingMoneyService {

	private final SprinklingMoneyRepository sprinklingMoneyRepository;
	private final MessageRoomService messageRoomService;
	private final SprinklingMoneyReceiveService receiveService;
	private final SprinklingMoneyDivider sprinklingMoneyDivider;
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
	 * 페이머니 뿌리기 추가
	 * @param sprinklingMoney
	 * @return
	 */
	@Transactional
	public String createSprinklingMoney(SprinklingMoney sprinklingMoney) {
		final MessageRoom messageRoom = messageRoomService.getMessageRoom(sprinklingMoney.getMessageRoomId());

		final List<User> othersInMessageRoom = getOthersInMessageRoom(sprinklingMoney, messageRoom);
		final String token = tokenGenerator.makeToken(messageRoom);

		sprinklingMoney.setToken(token);
		final SprinklingMoney saved = sprinklingMoneyRepository.save(sprinklingMoney);

		List<DividedMoney> dividedMoneyList = sprinklingMoneyDivider.divide(sprinklingMoney, othersInMessageRoom);
		receiveService.saveSprinklingMoneyList(
			dividedMoneyList.stream()
			.map(m -> SprinklingMoneyReceive.create(saved, m.getMember().getId(), m.getAmount()))
			.collect(Collectors.toList())
		);

		return token;
	}

	/**
	 * 자신을 제외한 메시지방 멤버 조회
	 * @param sprinkling
	 * @param messageRoom
	 * @return
	 */
	List<User> getOthersInMessageRoom(SprinklingMoney sprinkling, MessageRoom messageRoom) {
		return messageRoom.getMemberList()
			.stream()
			.filter(m -> !StringUtils.equals(sprinkling.getUserId(), m.getId()))
			.collect(Collectors.toList());
	}
}
