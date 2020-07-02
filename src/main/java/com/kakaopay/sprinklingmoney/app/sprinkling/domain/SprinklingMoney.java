package com.kakaopay.sprinklingmoney.app.sprinkling.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kakaopay.sprinklingmoney.app.common.exception.ErrorCode;
import com.kakaopay.sprinklingmoney.app.common.exception.SprinklingMoneyException;
import com.kakaopay.sprinklingmoney.app.receive.domain.SprinklingMoneyReceive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@EntityListeners({AuditingEntityListener.class})
@Entity
@Table(name = "sprinkling_money")
@JsonIgnoreProperties(value= {"receiveList"})
@EqualsAndHashCode
public class SprinklingMoney {
	private static final int EXPIRED_MINUTE = 10;

	@Id
	@GeneratedValue
	private int no;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "message_room_id")
	private String messageRoomId;

	@Column(name = "receiver_count")
	private int receiverCount;

	@Column
	private long amount;

	@Column
	private String token;

	@Column(name = "expire_date")
	private LocalDateTime expireDate;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@OneToMany(mappedBy = "sprinklingMoney", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<SprinklingMoneyReceive> receiveList = new ArrayList<>();

	@Builder(toBuilder = true)
	public SprinklingMoney(String userId, String messageRoomId, Integer receiverCount, Long amount) {
		try {
			validate(userId, messageRoomId, receiverCount, amount);
		} catch (NullPointerException | IllegalArgumentException e) {
			throw SprinklingMoneyException.builder()
				.errorCode(ErrorCode.MISSING_PARAMETER)
				.message("Missing Parameter")
				.cause(e)
				.build();
		}

		this.userId = userId;
		this.messageRoomId = messageRoomId;
		this.receiverCount = receiverCount;
		this.amount = amount;
		this.createdDate = LocalDateTime.now();
		this.expireDate = this.createdDate.plusMinutes(EXPIRED_MINUTE);
	}

	private void validate(String userId, String messageRoomId, Integer receiverCount, Long amount) {
		Objects.requireNonNull(userId);
		Objects.requireNonNull(messageRoomId);
		Objects.requireNonNull(receiverCount);
		Objects.requireNonNull(amount);
		if(receiverCount < 1 || amount < 1) {
			throw new IllegalArgumentException();
		}
	}

	@JsonIgnore
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(this.expireDate);
	}

	@JsonIgnore
	public boolean isCompleted() {
		return this.receiverCount == this.receiveList.stream().count();
	}


}
