package com.crossover.airline.resource.input;

import com.crossover.airline.resource.BaseInput;

public class CreditCardPaymentInput extends BaseInput {

	private static final long serialVersionUID = -3913754316493218480L;

	private Long onwardBookingId;

	private Long returnBookingId;
	
	private String name;
	
	private String creditCardNumber;
	
	private String expiryDate;
	
	private String cvvNumber;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCvvNumber() {
		return cvvNumber;
	}

	public void setCvvNumber(String cvvNumber) {
		this.cvvNumber = cvvNumber;
	}

	public Long getOnwardBookingId() {
		return onwardBookingId;
	}

	public void setOnwardBookingId(Long onwardBookingId) {
		this.onwardBookingId = onwardBookingId;
	}

	public Long getReturnBookingId() {
		return returnBookingId;
	}

	public void setReturnBookingId(Long returnBookingId) {
		this.returnBookingId = returnBookingId;
	}
}
