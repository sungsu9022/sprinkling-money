package com.kakaopay.sprinklingmoney.app.controller.sprinkling;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;
import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
class MoneySprinklingAndReceiver {
	private LocalDateTime sprinklingDate;
	private long totalAmount;
	private long receiveCompletedAmount;
	private List<ReceiveCompletedMember> receiveCompletedMemberList;

	public static MoneySprinklingAndReceiver create(SprinklingMoney money, List<SprinklingMoneyReceive> receiveList) {
		final List<SprinklingMoneyReceive> receivedList = receiveList.stream()
			.filter(SprinklingMoneyReceive::isAlreadyReceive)
			.collect(Collectors.toList());

		return builder()
			.sprinklingDate(money.getCreatedDate())
			.totalAmount(money.getAmount())
			.receiveCompletedAmount(receivedList.stream().mapToLong(SprinklingMoneyReceive::getMoney).sum())
			.receiveCompletedMemberList(convertReceiveCompletedMemberList(receivedList))
			.build();
	}

	private static List<ReceiveCompletedMember> convertReceiveCompletedMemberList(List<SprinklingMoneyReceive> receivedList) {
		return receivedList.stream()
			.map(ReceiveCompletedMember::create)
			.collect(Collectors.toList());
	}


	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	private static class ReceiveCompletedMember {
		private String userId;
		private long amount;
		private LocalDateTime receiveDate;

		public static ReceiveCompletedMember create(SprinklingMoneyReceive receive) {
			return new ReceiveCompletedMember(receive.getUserId(), receive.getMoney(), receive.getReceiveDate());
		}
	}
}
