package com.crossover.airline.event.handler;

import static com.crossover.airline.common.Constants.TICKET_GENERATED_QUEUE_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.event.TicketGeneratedEvent;
import com.crossover.airline.repository.BookingRepository;
import com.crossover.airline.service.MailService;

@Component
public class TicketGeneratedEventHandler {

	private final Logger logger = LoggerFactory.getLogger(TicketGeneratedEventHandler.class);
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@JmsListener(destination = TICKET_GENERATED_QUEUE_NAME)
	public void ticketGenerated(TicketGeneratedEvent event) {
		logger.info("Ticket generated handler for booking ID - {}, Path to the ticket - {}", event.getBookingId(), event.getPathToTicket());
		
		Booking booking = bookingRepository.findOne(event.getBookingId());
		
		mailService.emailTickets(event.getEmail(), booking.getName(), event.getPathToTicket());
	}
}
