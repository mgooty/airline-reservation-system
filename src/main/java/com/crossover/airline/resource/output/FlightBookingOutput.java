package com.crossover.airline.resource.output;

import com.crossover.airline.resource.BaseOutput;

public class FlightBookingOutput extends BaseOutput {

	private static final long serialVersionUID = -2300804627172499341L;
	
	private Long onwardBookingId;

	private Long returnBookingId;
	
	private int numOfSeats;
	
	private Double totalBookingAmount;
	
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

	public int getNumOfSeats() {
		return numOfSeats;
	}

	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}

	public Double getTotalBookingAmount() {
		return totalBookingAmount;
	}

	public void setTotalBookingAmount(Double totalBookingAmount) {
		this.totalBookingAmount = totalBookingAmount;
	}
}
