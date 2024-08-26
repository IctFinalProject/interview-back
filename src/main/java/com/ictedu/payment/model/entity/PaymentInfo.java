package com.ictedu.payment.model.entity;

import java.time.LocalDateTime;

import com.ictedu.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment")
public class PaymentInfo {

	@Id
	@SequenceGenerator(name="seq_payinfo_id",sequenceName = "seq_payinfo_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_payinfo_id")
	private Long id;
	
    // User 엔티티와의 관계 설정
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userId;
	
	@Column(name = "price", nullable = false)
	private Integer price;
	
	@Column(name = "order_id", nullable = false)
	private String orderId;
	
	@Column(name = "payment_key", nullable = false)
	private String paymentKey;
	
	@Column(name = "order_name", nullable = false)
	private String orderName;
	
	@Column(name = "pay_method", nullable = false)
	private String payMethod;
	
	@Column(name = "pay_status", nullable = false)
	private String payStatus;
	
    @Column(name = "is_successed")
    private Integer isSuccessed = 0;
    
    @Column(name = "is_canceled")
    private Integer isCanceled = 0;
    
    @Column(name = "pay_requested")
    private LocalDateTime requestedAt;
    
    @Column(name = "pay_approved")
    private LocalDateTime approvedAt;
	
	@Column(name = "last_transactionKey")
	private String lastTransactionKey;
	
	@Column(name = "pay_cancels")
	private String payCancels;
	
	@Column(name = "canceled_at")
	private LocalDateTime canceledAt;
	
	@Column(name = "cancel_reason")
	private String cancelReason;
	
	@Column(name = "cancel_status")
	private String cancelStatus;
	
	@Column(name = "pay_secret", nullable = false)
	private String paySecret;
	
    @Column(name = "use_count")
    private Integer useCount = 0;
	
}
