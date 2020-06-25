package com.kakaopay.sprinklingmoney.app.messageroom;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

	/**
	 * 대화방 조회
	 *  - DB에서 데이터를 읽는다고 가정하고 dummy return
	 * @param id
	 * @return
	 */
	public MessageRoom getMessageRoom(String id) {
		return MessageRoom.dummyMessageRoom(id);
	}

	/**
	 * roomId 유효성 체크
	 * @param roomId
	 * @return
	 */
	public boolean isValidMessageRoom(String roomId) {
		return Optional.ofNullable(roomId)
			.filter(StringUtils::isNotBlank)
			.map(id -> Boolean.TRUE)
			.orElse(Boolean.FALSE);
	}
}
