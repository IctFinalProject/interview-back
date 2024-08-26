package com.ictedu.payment.service;

import com.ictedu.user.model.entity.User;

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
    private Long changedId; // 추가된 필드
    private User user;      // 추가된 필드

    // 필요한 필드만 초기화하는 생성자
    public InputPayment(String orderId) {
        this.orderId = orderId;
    }

    // 모든 필드를 초기화하는 생성자
    public InputPayment(Integer price, String orderId, String paymentKey, String orderName, String payMethod,
                        String payStatus, String lastTransactionKey, String payCancels, String paySecret,
                        String requestedAt, String approvedAt, Long changedId, User user) {
        this.price = price;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.payMethod = payMethod;
        this.payStatus = payStatus;
        this.lastTransactionKey = lastTransactionKey;
        this.payCancels = payCancels;
        this.paySecret = paySecret;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.changedId = changedId;
        this.user = user;
    }
    
    // changedId까지 포함하는 생성자 (user 필드 없이)
    public InputPayment(Integer price, String orderId, String paymentKey, String orderName, String payMethod,
                        String payStatus, String lastTransactionKey, String payCancels, String paySecret,
                        String requestedAt, String approvedAt, Long changedId) {
        this.price = price;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.payMethod = payMethod;
        this.payStatus = payStatus;
        this.lastTransactionKey = lastTransactionKey;
        this.payCancels = payCancels;
        this.paySecret = paySecret;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.changedId = changedId;
    }
}