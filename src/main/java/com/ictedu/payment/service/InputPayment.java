package com.ictedu.payment.service;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputPayment {
	private Integer price;
	private String orderId;
	private String paymentKey;
	private String orderName;
	private String payMethod;
	private String payStatus;
	private String lastTransactionKey;
	private String payCancels;
	private String cancelReason;
	private String cancelStatus;
	private String paySecret;
	private String requestedAt;
	private String approvedAt;
	private String canceledAt;
	
    public InputPayment(String orderId) {
        this.orderId = orderId;
    }

}
