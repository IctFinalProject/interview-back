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
	
	Optional<PaymentInfo> findByorderId(String orderId);
	
	Optional<PaymentInfo> findByPaymentKey(String paymentKey);
}
