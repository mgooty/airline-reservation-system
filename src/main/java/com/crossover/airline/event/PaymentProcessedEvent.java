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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((onwardBookingId == null) ? 0 : onwardBookingId.hashCode());
		result = prime * result + ((onwardFlightId == null) ? 0 : onwardFlightId.hashCode());
		result = prime * result + ((paymentTxnId == null) ? 0 : paymentTxnId.hashCode());
		result = prime * result + ((returnBookingId == null) ? 0 : returnBookingId.hashCode());
		result = prime * result + ((returnFlightId == null) ? 0 : returnFlightId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaymentProcessedEvent other = (PaymentProcessedEvent) obj;
		if (onwardBookingId == null) {
			if (other.onwardBookingId != null)
				return false;
		} else if (!onwardBookingId.equals(other.onwardBookingId))
			return false;
		if (onwardFlightId == null) {
			if (other.onwardFlightId != null)
				return false;
		} else if (!onwardFlightId.equals(other.onwardFlightId))
			return false;
		if (paymentTxnId == null) {
			if (other.paymentTxnId != null)
				return false;
		} else if (!paymentTxnId.equals(other.paymentTxnId))
			return false;
		if (returnBookingId == null) {
			if (other.returnBookingId != null)
				return false;
		} else if (!returnBookingId.equals(other.returnBookingId))
			return false;
		if (returnFlightId == null) {
			if (other.returnFlightId != null)
				return false;
		} else if (!returnFlightId.equals(other.returnFlightId))
			return false;
		return true;
	}
}
