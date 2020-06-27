package com.kakaopay.sprinklingmoney.app.controller.sprinkling;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakaopay.sprinklingmoney.app.common.models.ResponseModel;
import com.kakaopay.sprinklingmoney.app.common.models.SuccessResponse;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.receive.service.SprinklingMoneyReceiveService;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;
import com.kakaopay.sprinklingmoney.app.sprinkling.service.SprinklingMoneyService;
import com.kakaopay.sprinklingmoney.app.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SprinklingMoneyController {
	private final SprinklingMoneyService moneyService;
	private final SprinklingMoneyReceiveService receiveService;


	/**
	 * 1. 뿌리기 API
	 * @param sprinklingMoneyRequest
	 * @param user
	 * @param roomId
	 * @return
	 */
	@PostMapping("/money/sprinkling")
	public ResponseModel postMoneySprinkling(
		@RequestBody SprinklingMoneyRequest sprinklingMoneyRequest
		, User user
		, String roomId
	) {

		final String token = moneyService.createSprinklingMoney(SprinklingMoney.builder()
			.userId(user.getId())
			.messageRoomId(roomId)
			.amount(sprinklingMoneyRequest.getAmount())
			.receiverCount(sprinklingMoneyRequest.getReceiverCount())
			.build()
		);
		return new SuccessResponse<>(new MoneySprinklingToken(token));
	}

	/**
	 * 2. 받기 API
	 * @param token
	 * @return
	 */
	@PutMapping("/money/sprinkling/receive/{token}")
	public ResponseModel putMoneySprinklingReceive(@PathVariable String token, User user, String roomId) {
		final SprinklingMoney sprinklingMoney = moneyService.getSprinklingMoney(token, roomId);
		final SprinklingMoneyReceive receive = receiveService.receive(sprinklingMoney, user.getId());
		return new SuccessResponse(new MoneySprinklingAmount(receive.getMoney()));
	}

	/**
	 * 3. 조회 API
	 * @param token
	 * @return
	 */
	@GetMapping("/money/sprinkling/receive/{token}")
	public ResponseModel getMoneySprinklingReceive(@PathVariable String token, User user, String roomId) {
		final SprinklingMoney money = moneyService.getSprinklingMoney(token, user.getId(), roomId);
		final List<SprinklingMoneyReceive> receiveList = money.getReceiveList();
		return new SuccessResponse(MoneySprinklingAndReceiver.create(money,receiveList));
	}
}
