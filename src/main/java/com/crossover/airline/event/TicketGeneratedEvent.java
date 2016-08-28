package com.crossover.airline.event;

public class TicketGeneratedEvent extends BaseEvent {

	private static final long serialVersionUID = -3455393558045215921L;

	private String email;
	
	private String pathToTicket;
	
	private Long bookingId;

	public TicketGeneratedEvent(String email, String pathToTicket, Long bookingId) {
		this.email = email;
		this.pathToTicket = pathToTicket;
		this.bookingId = bookingId;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPathToTicket() {
		return pathToTicket;
	}

	public void setPathToTicket(String pathToTicket) {
		this.pathToTicket = pathToTicket;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}
}
