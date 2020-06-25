package com.kakaopay.sprinklingmoney.app.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class UserServiceTest {
	private UserService service = new UserService();

	@Test
	public void isValidUser() {
		assertFalse(service.isValidUser(null));
		assertFalse(service.isValidUser(""));
		assertTrue(service.isValidUser("id"));
	}

}