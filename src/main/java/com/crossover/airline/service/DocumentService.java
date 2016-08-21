package com.crossover.airline.service;

public interface DocumentService {

	public String generateTicketAsPdf(Long bookingId, Long paymentTxnId, Long flightId) throws Exception;

	public String generateTicketAsPdf(Long onwardBookingId, Long returnBookingId, Long paymentTxnId, Long onwardFlightId, Long returnFlightId)  throws Exception;
}
