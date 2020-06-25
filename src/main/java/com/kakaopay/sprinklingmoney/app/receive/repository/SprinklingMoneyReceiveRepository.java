package com.kakaopay.sprinklingmoney.app.receive.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;

@Repository
public interface SprinklingMoneyReceiveRepository extends JpaRepository<SprinklingMoneyReceive, Integer> {
	Optional<SprinklingMoneyReceive> findByNo(int no);
	Optional<SprinklingMoneyReceive> findByUserIdAndSprinklingMoneyNo(String userId, int sprinklingMoneyNo);
	List<SprinklingMoneyReceive> findAllBySprinklingMoneyNo(int sprinklingMoneyNo);
}
