package com.kakaopay.sprinklingmoney.app.sprinkling.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

@Repository
public interface SprinklingMoneyRepository extends JpaRepository<SprinklingMoney, Integer> {
	Optional<SprinklingMoney> findByTokenAndMessageRoomId(String token, String messageRoomId);
	Optional<SprinklingMoney> findByTokenAndUserIdAndMessageRoomId(String token, String userId, String messageRoomId);
}
