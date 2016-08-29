package com.crossover.airline.resource.output;

import com.crossover.airline.entity.PaymentTxn.PaymentStatus;
import com.crossover.airline.resource.BaseOutput;

public class BookingPaymentOutput extends BaseOutput {

	private static final long serialVersionUID = -8624620083383135289L;
	
	private Long paymentTxnId;
	
	private PaymentStatus status;

	public Long getPaymentTxnId() {
		return paymentTxnId;
	}

	public void setPaymentTxnId(Long paymentTxnId) {
		this.paymentTxnId = paymentTxnId;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
	
}
