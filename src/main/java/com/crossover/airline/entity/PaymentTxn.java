package com.crossover.airline.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class PaymentTxn extends BaseEntity {

	private static final long serialVersionUID = -5141291261446634846L;

	public enum PaymentMode {
		CREDIT_CARD, DEBIT_CARD, INTERNET_BANKING
	}
	
	public enum PaymentStatus {
		SUCCESS, FAILED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "onward_booking_id")
	private Booking onwardBooking;
	
	@OneToOne
	@JoinColumn(name = "return_booking_id", nullable = true)
	private Booking returnBooking;
	
	@Column(name = "payment_mode", nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentMode paymentMode;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Column(name = "total_amount")
	private Double totalAmount;
	
	@Column(name = "txn_ref_number")
	private String txnRefNumber;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Booking getOnwardBooking() {
		return onwardBooking;
	}

	public void setOnwardBooking(Booking onwardBooking) {
		this.onwardBooking = onwardBooking;
	}

	public Booking getReturnBooking() {
		return returnBooking;
	}

	public void setReturnBooking(Booking returnBooking) {
		this.returnBooking = returnBooking;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTxnRefNumber() {
		return txnRefNumber;
	}

	public void setTxnRefNumber(String txnRefNumber) {
		this.txnRefNumber = txnRefNumber;
	}
	
}
