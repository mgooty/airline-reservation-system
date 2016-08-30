package com.crossover.airline.resource.output;

import java.util.List;

public class BookingDetailsOutput {

	private List<BookingRecord> bookings;
	
	public List<BookingRecord> getBookings() {
		return bookings;
	}

	public void setBookings(List<BookingRecord> bookings) {
		this.bookings = bookings;
	}

	public static class BookingRecord {
		FlightBookingOutput booking;
		
		FlightOutput flight;
		
		List<FlightSeatOutput> seats;
		
		BookingPaymentOutput payment;
		
		List<PassengerOutput> passengers;

		public FlightBookingOutput getBooking() {
			return booking;
		}

		public void setBooking(FlightBookingOutput booking) {
			this.booking = booking;
		}

		public FlightOutput getFlight() {
			return flight;
		}

		public void setFlight(FlightOutput flight) {
			this.flight = flight;
		}

		public List<FlightSeatOutput> getSeats() {
			return seats;
		}

		public void setSeats(List<FlightSeatOutput> seats) {
			this.seats = seats;
		}

		public BookingPaymentOutput getPayment() {
			return payment;
		}

		public void setPayment(BookingPaymentOutput payment) {
			this.payment = payment;
		}

		public List<PassengerOutput> getPassengers() {
			return passengers;
		}

		public void setPassengers(List<PassengerOutput> passengers) {
			this.passengers = passengers;
		}
	}
}
