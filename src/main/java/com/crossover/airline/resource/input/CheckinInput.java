package com.crossover.airline.resource.input;

import java.util.List;

public class CheckinInput {

	private List<FlightSeatInput> seats;
	
	public List<FlightSeatInput> getSeats() {
		return seats;
	}

	public void setSeats(List<FlightSeatInput> seats) {
		this.seats = seats;
	}

	public static class FlightSeatInput {
		private Long seatId;
		
		private int seatNumber;
		
		private Long passengerId;

		public Long getSeatId() {
			return seatId;
		}

		public void setSeatId(Long seatId) {
			this.seatId = seatId;
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
}
