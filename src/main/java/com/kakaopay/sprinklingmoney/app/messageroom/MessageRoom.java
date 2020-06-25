package com.kakaopay.sprinklingmoney.app.messageroom;

import java.util.List;

import com.kakaopay.sprinklingmoney.app.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MessageRoom {
	private String id;
	private List<User> memberList;

	public static MessageRoom dummyMessageRoom(String id) {
		final MessageRoom messageRoom = new MessageRoom();
		messageRoom.id = id;
		messageRoom.memberList = User.createDummyUserList();
		return messageRoom;
	}
}
