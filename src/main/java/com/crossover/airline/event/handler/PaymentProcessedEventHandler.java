package com.crossover.airline.event.handler;

import static com.crossover.airline.common.Constants.PAYMENT_PROCESSED_QUEUE_NAME;
import static com.crossover.airline.common.Constants.TICKET_GENERATED_QUEUE_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.event.PaymentProcessedEvent;
import com.crossover.airline.event.TicketGeneratedEvent;
import com.crossover.airline.repository.BookingRepository;
import com.crossover.airline.service.DocumentService;

@Component
public class PaymentProcessedEventHandler {

	private final Logger logger = LoggerFactory.getLogger(PaymentProcessedEventHandler.class);
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@JmsListener(destination = PAYMENT_PROCESSED_QUEUE_NAME)
	public void paymentProcessed(PaymentProcessedEvent event) {
		logger.info("Process event handler for booking ID - {}, Flight ID - {}, Payment Txn ID - {}", 
											event.getOnwardBookingId(), event.getOnwardFlightId(), event.getPaymentTxnId());
		
		String pathToTicket = "";
		Booking onwardBooking = bookingRepository.findOne(event.getOnwardBookingId());
		
		if(event.getReturnBookingId() == null) {
			try {
				pathToTicket = documentService.generateTicketAsPdf(event.getOnwardBookingId(), event.getPaymentTxnId(), event.getOnwardFlightId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				pathToTicket = documentService.generateTicketAsPdf(event.getOnwardBookingId(), event.getReturnBookingId(), event.getPaymentTxnId(), 
													event.getOnwardFlightId(), event.getReturnFlightId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(StringUtils.hasText(pathToTicket)) {
			TicketGeneratedEvent ticketGeneratedEvent = new TicketGeneratedEvent(onwardBooking.getEmail(), pathToTicket, onwardBooking.getId());
			jmsTemplate.convertAndSend(TICKET_GENERATED_QUEUE_NAME, ticketGeneratedEvent);
		} else {
			// TODO: log error message
		}
	}
	
}
