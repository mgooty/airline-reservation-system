package com.crossover.airline.event;

public class PaymentProcessedEvent extends BaseEvent {

	private static final long serialVersionUID = 529882125002503292L;

	private Long onwardFlightId;
	
	private Long onwardBookingId;
	
	private Long returnFlightId;
	
	private Long returnBookingId;
	
	private Long paymentTxnId;

	public PaymentProcessedEvent(Long onwardFlightId, Long onwardBookingId, Long returnFlightId, Long returnBookingId, Long paymentTxnId) {
		this.onwardFlightId = onwardFlightId;
		this.onwardBookingId = onwardBookingId;
		this.returnFlightId = returnFlightId;
		this.returnBookingId = returnBookingId;
		this.paymentTxnId = paymentTxnId;
	}
	
	public PaymentProcessedEvent(Long onwardFlightId, Long onwardBookingId, Long paymentTxnId) {
		this.onwardFlightId = onwardFlightId;
		this.onwardBookingId = onwardBookingId;
		this.paymentTxnId = paymentTxnId;
	}

	public Long getOnwardFlightId() {
		return onwardFlightId;
	}

	public void setOnwardFlightId(Long onwardFlightId) {
		this.onwardFlightId = onwardFlightId;
	}

	public Long getOnwardBookingId() {
		return onwardBookingId;
	}

	public void setOnwardBookingId(Long onwardBookingId) {
		this.onwardBookingId = onwardBookingId;
	}

	public Long getReturnFlightId() {
		return returnFlightId;
	}

	public void setReturnFlightId(Long returnFlightId) {
		this.returnFlightId = returnFlightId;
	}

	public Long getReturnBookingId() {
		return returnBookingId;
	}

	public void setReturnBookingId(Long returnBookingId) {
		this.returnBookingId = returnBookingId;
	}

	public Long getPaymentTxnId() {
		return paymentTxnId;
	}

	public void setPaymentTxnId(Long paymentTxnId) {
		this.paymentTxnId = paymentTxnId;
	}
	
	
}
