package com.crossover.airline.ext.payment.gateway;

import com.crossover.airline.entity.PaymentTxn.PaymentStatus;

public class PaymentGatewayOutput {

	private String referenceNum;
	
	private PaymentStatus status;

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
}
