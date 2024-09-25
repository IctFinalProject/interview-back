package com.ictedu.payment.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ictedu.payment.model.entity.PaymentInfo;
import com.ictedu.payment.repository.PaymentRepository;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import com.ictedu.user.service.InputUser;

@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	@Autowired
	private UserRepository userRepository;
	
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
    public PaymentInfo findByPaymentKey(String paymentKey) {
        return paymentRepository.findByPaymentKey(paymentKey);
    }
    
    // 특정 유저의 모든 결제 정보를 가져오기 위한 메서드
    @Transactional
    public List<PaymentInfo> findAllByUserId(User user) {
        return paymentRepository.findAllByUserId(user);
    }
    
    @Transactional
    public PaymentInfo paymentBasicInputUser(InputPayment inputPayment) {
        // changedId로 Users 테이블에서 userId를 조회
        Optional<User> optionalUser = userRepository.findById(inputPayment.getChangedId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            inputPayment.setUser(user); // inputPayment에 User 객체 추가
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID: " + inputPayment.getChangedId());
        }

        // PaymentInfo 생성 및 저장
        return paymentRepository.save(createBasicPayment(inputPayment));
    }

    @Transactional
    public PaymentInfo paymentPremiumInputUser(InputPayment inputPayment) {
        // Premium 결제에서도 동일하게 처리
        Optional<User> optionalUser = userRepository.findById(inputPayment.getChangedId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            inputPayment.setUser(user); // inputPayment에 User 객체 추가
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID: " + inputPayment.getChangedId());
        }

        return paymentRepository.save(createPremiumPayment(inputPayment));
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
        System.out.println("빌드 직전!");
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
                .userId(payment.getUser()) // User 객체 전달
                .build();
    }
    
    public PaymentInfo createPremiumPayment(InputPayment payment) {
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
        		.useCount(20)
        		.paymentKey(payment.getPaymentKey())
        		.requestedAt(requestedDateTime)
        		.approvedAt(approvedDateTime)
        		.canceledAt(canceledDateTime)
        		.userId(payment.getUser()) // User 객체 전달
                .build();
    }

	public List<PaymentInfo> findAll() {
		return paymentRepository.findAll();
	}
	
    public void minusBasicPlanUseCount(Optional<User> getUser) {
        // "베이직플랜"을 가진 결제 정보를 조회
    	// userId, orderName, isCanceled가 0이고, useCount가 1 이상인 결제 정보 조회
        Optional<PaymentInfo> paymentInfoOpt = paymentRepository.findByUserIdAndOrderNameAndIsCanceledAndUseCountGreaterThanEqual(getUser, "베이직플랜", 0, 1);
        
        if (paymentInfoOpt.isPresent()) {
            PaymentInfo paymentInfo = paymentInfoOpt.get();
            // useCount가 0보다 클 경우만 감소
            if (paymentInfo.getUseCount() > 0) {
                paymentInfo.setUseCount(paymentInfo.getUseCount() - 1);
                // 변경된 값을 저장
                paymentRepository.save(paymentInfo);
                System.out.println("베이직플랜 useCount 감소 완료");
            } else {
                System.out.println("useCount가 0 이하입니다. 감소할 수 없습니다.");
            }
        } else {
            System.out.println("베이직플랜 결제를 찾을 수 없습니다.");
        }
    }
    
    public void minusPremiumPlanUseCount(Optional<User> getUser) {
        // "프리미엄플랜"을 가진 결제 정보를 조회
    	Optional<PaymentInfo> paymentInfoOpt = paymentRepository.findByUserIdAndOrderNameAndIsCanceledAndUseCountGreaterThanEqual(getUser, "프리미엄플랜", 0, 1);
        
        if (paymentInfoOpt.isPresent()) {
            PaymentInfo paymentInfo = paymentInfoOpt.get();
            // useCount가 0보다 클 경우만 감소
            if (paymentInfo.getUseCount() > 0) {
                paymentInfo.setUseCount(paymentInfo.getUseCount() - 1);
                // 변경된 값을 저장
                paymentRepository.save(paymentInfo);
                System.out.println("프리미엄플랜 useCount 감소 완료");
            } else {
                System.out.println("useCount가 0 이하입니다. 감소할 수 없습니다.");
            }
        } else {
            System.out.println("프리미엄플랜 결제를 찾을 수 없습니다.");
        }
    }
}
