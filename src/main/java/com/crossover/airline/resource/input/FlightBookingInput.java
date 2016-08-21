package com.crossover.airline.resource.input;

import com.crossover.airline.resource.BaseInput;

public class FlightBookingInput extends BaseInput {

	private static final long serialVersionUID = -1015599584496713984L;
	
	private Integer numOfSeats;
	
	private String mobileNum;
	
	private String email;
	
	private String name;
	
	private Long onwardFlightId;
	
	private Long returnFlightId;

	public Integer getNumOfSeats() {
		return numOfSeats;
	}

	public void setNumOfSeats(Integer numOfSeats) {
		this.numOfSeats = numOfSeats;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getOnwardFlightId() {
		return onwardFlightId;
	}

	public void setOnwardFlightId(Long onwardFlightId) {
		this.onwardFlightId = onwardFlightId;
	}

	public Long getReturnFlightId() {
		return returnFlightId;
	}

	public void setReturnFlightId(Long returnFlightId) {
		this.returnFlightId = returnFlightId;
	}
}
