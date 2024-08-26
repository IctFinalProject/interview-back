package com.ictedu.payment.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ictedu.payment.model.entity.PaymentInfo;
import com.ictedu.payment.repository.PaymentRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.InputUser;

@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	
	public PaymentService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}
	
    @Transactional
    public Optional<PaymentInfo> findByUserId(User userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    @Transactional
    public Optional<PaymentInfo> findByorderId(String orderId) {
        return paymentRepository.findByorderId(orderId);
    }
    
    @Transactional
    public Optional<PaymentInfo> findByPaymentKey(String paymentKey) {
        return paymentRepository.findByPaymentKey(paymentKey);
    }
    

    private PaymentInfo createBasicPayment(InputPayment payment) {
        String requestedAt = payment.getRequestedAt();
        String approvedAt = payment.getApprovedAt();
        String canceledAt = payment.getCanceledAt();
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        LocalDateTime requestedDateTime = null;
        LocalDateTime approvedDateTime = null;
        LocalDateTime canceledDateTime = null;

        if (requestedAt != null) {
            OffsetDateTime requestedOffsetDateTime = OffsetDateTime.parse(requestedAt, formatter);
            requestedDateTime = requestedOffsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        if (approvedAt != null) {
            OffsetDateTime approvedOffsetDateTime = OffsetDateTime.parse(approvedAt, formatter);
            approvedDateTime = approvedOffsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        if (canceledAt != null) {
            OffsetDateTime canceledOffsetDateTime = OffsetDateTime.parse(canceledAt, formatter);
            canceledDateTime = canceledOffsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }
        
        return PaymentInfo.builder()
        		.orderId(payment.getOrderId())
        		.orderName(payment.getOrderName())
        		.price(payment.getPrice())
        		.paySecret(payment.getPaySecret())
        		.payCancels(payment.getPayCancels())
        		.payMethod(payment.getPayMethod())
        		.payStatus(payment.getPayStatus())
        		.cancelReason(payment.getCancelReason())
        		.cancelStatus(payment.getCancelStatus())
        		.isCanceled(0)
        		.isSuccessed(1)
        		.lastTransactionKey(payment.getLastTransactionKey())
        		.useCount(10)
        		.paymentKey(payment.getPaymentKey())
        		.requestedAt(requestedDateTime)
        		.approvedAt(approvedDateTime)
        		.canceledAt(canceledDateTime)
                .build();
    }
    
    private PaymentInfo createPremiumPayment(InputPayment payment) {
        String requestedAt = payment.getRequestedAt();
        String approvedAt = payment.getApprovedAt();
        String canceledAt = payment.getCanceledAt();
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        LocalDateTime requestedDateTime = null;
        LocalDateTime approvedDateTime = null;
        LocalDateTime canceledDateTime = null;

        if (requestedAt != null) {
            OffsetDateTime requestedOffsetDateTime = OffsetDateTime.parse(requestedAt, formatter);
            requestedDateTime = requestedOffsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        if (approvedAt != null) {
            OffsetDateTime approvedOffsetDateTime = OffsetDateTime.parse(approvedAt, formatter);
            approvedDateTime = approvedOffsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        if (canceledAt != null) {
            OffsetDateTime canceledOffsetDateTime = OffsetDateTime.parse(canceledAt, formatter);
            canceledDateTime = canceledOffsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }
        
        return PaymentInfo.builder()
        		.orderId(payment.getOrderId())
        		.orderName(payment.getOrderName())
        		.price(payment.getPrice())
        		.paySecret(payment.getPaySecret())
        		.payCancels(payment.getPayCancels())
        		.payMethod(payment.getPayMethod())
        		.payStatus(payment.getPayStatus())
        		.cancelReason(payment.getCancelReason())
        		.cancelStatus(payment.getCancelStatus())
        		.isCanceled(0)
        		.isSuccessed(1)
        		.lastTransactionKey(payment.getLastTransactionKey())
        		.useCount(21)
        		.paymentKey(payment.getPaymentKey())
        		.requestedAt(requestedDateTime)
        		.approvedAt(approvedDateTime)
        		.canceledAt(canceledDateTime)
                .build();
    }
}
