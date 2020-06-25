package com.kakaopay.sprinklingmoney.app.receive.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.kakaopay.sprinklingmoney.app.sprinkling.domain.SprinklingMoney;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners({AuditingEntityListener.class})
@Entity
@Table(name = "sprinkling_money_receive")
public class SprinklingMoneyReceive {
	@Id
	@GeneratedValue
	private int no;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sprinkling_money_no")
	private SprinklingMoney sprinklingMoney;

	@Column(name = "user_id")
	private String userId;

	@Column
	private long money;

	@CreatedDate
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "receive_date")
	private LocalDateTime receiveDate;

	public static SprinklingMoneyReceive create(SprinklingMoney sprinklingMoney, String userId, long money) {
		final SprinklingMoneyReceive newInstance = new SprinklingMoneyReceive();
		newInstance.sprinklingMoney = sprinklingMoney;
		newInstance.userId = userId;
		newInstance.money = money;
		newInstance.createdDate = LocalDateTime.now();
		return newInstance;
	}

	public boolean isAlreadyReceive() {
		return Objects.nonNull(this.receiveDate);
	}
}
