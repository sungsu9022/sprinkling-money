package com.kakaopay.sprinklingmoney.app.messageroom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;


public class MessageRoomServiceTest {
	MessageRoomService messageRoomService = new MessageRoomService();

	@Test
	public void isValidMessageRoomTest() {
		assertFalse(messageRoomService.isValidMessageRoom(null));
		assertTrue(messageRoomService.isValidMessageRoom("3a87933c-ea2a-4667-8cd2-dcd076c07eb4"));
	}


}