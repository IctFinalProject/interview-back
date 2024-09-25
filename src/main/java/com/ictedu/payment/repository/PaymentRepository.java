package com.ictedu.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.payment.model.entity.PaymentInfo;
import com.ictedu.user.model.entity.User;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentInfo, Long>{

	Optional<PaymentInfo> findByUserId(User userId);
	
    // 특정 유저의 모든 결제 정보를 가져오기 위한 메서드 추가
    List<PaymentInfo> findAllByUserId(User userId);
	
	Optional<PaymentInfo> findByorderId(String orderId);
	
	PaymentInfo findByPaymentKey(String paymentKey);
	
	// userId, orderName, isCanceled, useCount 값을 조건으로 조회
    Optional<PaymentInfo> findByUserIdAndOrderNameAndIsCanceledAndUseCountGreaterThanEqual(Optional<User> getUser, String orderName, Integer isCanceled, Integer useCount);

}
