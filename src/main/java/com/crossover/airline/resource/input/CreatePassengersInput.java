package com.crossover.airline.resource.input;

import java.util.List;

public class CreatePassengersInput {

	private BookingPassengerRecord onwardBookingPassengerRecord;
	
	private BookingPassengerRecord returnBookingPassengerRecord;
	
	public BookingPassengerRecord getOnwardBookingPassengerRecord() {
		return onwardBookingPassengerRecord;
	}

	public void setOnwardBookingPassengerRecord(BookingPassengerRecord onwardBookingPassengerRecord) {
		this.onwardBookingPassengerRecord = onwardBookingPassengerRecord;
	}

	public BookingPassengerRecord getReturnBookingPassengerRecord() {
		return returnBookingPassengerRecord;
	}

	public void setReturnBookingPassengerRecord(BookingPassengerRecord returnBookingPassengerRecord) {
		this.returnBookingPassengerRecord = returnBookingPassengerRecord;
	}

	public static class BookingPassengerRecord {
		private List<PassengerInput> passengers;
		
		private Long bookingId;

		public List<PassengerInput> getPassengers() {
			return passengers;
		}

		public void setPassengers(List<PassengerInput> passengers) {
			this.passengers = passengers;
		}

		public Long getBookingId() {
			return bookingId;
		}

		public void setBookingId(Long bookingId) {
			this.bookingId = bookingId;
		}
	}
}
