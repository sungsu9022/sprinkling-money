package com.kakaopay.sprinklingmoney.app.receive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;

@Repository
public interface SprinklingMoneyReceiveRepository extends JpaRepository<SprinklingMoneyReceive, Integer> {
}
