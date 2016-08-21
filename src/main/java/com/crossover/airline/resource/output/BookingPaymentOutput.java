package com.crossover.airline.resource.output;

import com.crossover.airline.resource.BaseOutput;

public class BookingPaymentOutput extends BaseOutput {

	private static final long serialVersionUID = -8624620083383135289L;
	
	private Long paymentTxnId;

	public Long getPaymentTxnId() {
		return paymentTxnId;
	}

	public void setPaymentTxnId(Long paymentTxnId) {
		this.paymentTxnId = paymentTxnId;
	}
	
	
}
