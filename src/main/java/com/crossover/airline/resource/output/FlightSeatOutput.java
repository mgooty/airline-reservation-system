package com.crossover.airline.resource.output;

import com.crossover.airline.resource.BaseOutput;

public class FlightSeatOutput extends BaseOutput {

	private static final long serialVersionUID = -3148386443229943096L;

	private Long seatId;
	
	private Long flightId;
	
	private int seatNumber;
	
	private Long passengerId;

	public Long getSeatId() {
		return seatId;
	}

	public void setSeatId(Long seatId) {
		this.seatId = seatId;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}
	
	
}
