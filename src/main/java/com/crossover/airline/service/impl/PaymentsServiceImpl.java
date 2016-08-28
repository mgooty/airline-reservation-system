package com.crossover.airline.service.impl;

import static com.crossover.airline.common.Constants.PAYMENT_PROCESSED_QUEUE_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Booking.BookingStatus;
import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.PaymentTxn;
import com.crossover.airline.entity.PaymentTxn.PaymentMode;
import com.crossover.airline.entity.PaymentTxn.PaymentStatus;
import com.crossover.airline.event.PaymentProcessedEvent;
import com.crossover.airline.repository.BookingRepository;
import com.crossover.airline.repository.FlightRepository;
import com.crossover.airline.repository.PaymentTxnRepository;
import com.crossover.airline.resource.input.CreditCardPaymentInput;
import com.crossover.airline.resource.output.BookingPaymentOutput;
import com.crossover.airline.service.PaymentsService;

@Service
public class PaymentsServiceImpl implements PaymentsService {

	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private PaymentTxnRepository paymentTxnRepository;
	
	@Autowired
	private FlightRepository flightRepository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Override
	public BookingPaymentOutput processCreditCardPayment(CreditCardPaymentInput creditCardPaymentInput) throws Exception {
		// TODO: integrate with credit card processing systems
		Booking onwardBooking = bookingRepository.findOne(creditCardPaymentInput.getOnwardBookingId());
		Booking returnBooking = null;
		
		if(creditCardPaymentInput.getReturnBookingId() != null && creditCardPaymentInput.getReturnBookingId() != 0) {
			returnBooking = bookingRepository.findOne(creditCardPaymentInput.getReturnBookingId());
		}
		
		PaymentTxn paymentTxn = new PaymentTxn();
		paymentTxn.setOnwardBooking(onwardBooking);
		paymentTxn.setReturnBooking(returnBooking != null ? returnBooking : null);
		paymentTxn.setPaymentMode(PaymentMode.CREDIT_CARD);
		paymentTxn.setPaymentStatus(PaymentStatus.SUCCESS);
		
		paymentTxn = paymentTxnRepository.save(paymentTxn);
		
		if(paymentTxn.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
			onwardBooking.setBookingStatus(BookingStatus.CONFIRMED);
			bookingRepository.save(onwardBooking);

			Flight onwardFlight = flightRepository.findOne(onwardBooking.getFlight().getId());
			onwardFlight.setNoOfSeatsBooked(onwardFlight.getNoOfSeatsBooked() + onwardBooking.getNumOfSeats());
			flightRepository.save(onwardFlight);

			Flight returnFlight = null;
			if(returnBooking != null) {
				returnBooking.setBookingStatus(BookingStatus.CONFIRMED);
				bookingRepository.save(returnBooking);
				
				returnFlight = flightRepository.findOne(returnBooking.getFlight().getId());
				returnFlight.setNoOfSeatsBooked(returnFlight.getNoOfSeatsBooked() + returnBooking.getNumOfSeats());
				flightRepository.save(returnFlight);
			}
			
			// TODO: push an event to the queue - PAYMENT_PROCESSED. Event handler will email tickets as PDF document
			PaymentProcessedEvent event = null;
			if(returnBooking == null) {
				event = new PaymentProcessedEvent(onwardBooking.getId(), paymentTxn.getId(), onwardFlight.getId());
			} else {
				event = new PaymentProcessedEvent(onwardFlight.getId(), onwardBooking.getId(), returnFlight.getId(), returnBooking.getId(), paymentTxn.getId());
			}
			jmsTemplate.convertAndSend(PAYMENT_PROCESSED_QUEUE_NAME, event);
		} else {
			onwardBooking.setBookingStatus(BookingStatus.PAYMENT_FAILED);
			bookingRepository.save(onwardBooking);
			
			Flight onwardFlight = flightRepository.findOne(onwardBooking.getFlight().getId());
			onwardFlight.setNoOfSeatsBooked(onwardFlight.getNoOfSeatsAvailable() + onwardBooking.getNumOfSeats());
			flightRepository.save(onwardFlight);
			
			if(returnBooking != null) {
				returnBooking.setBookingStatus(BookingStatus.PAYMENT_FAILED);
				bookingRepository.save(returnBooking);
				
				Flight returnFlight = flightRepository.findOne(returnBooking.getFlight().getId());
				returnFlight.setNoOfSeatsBooked(returnFlight.getNoOfSeatsAvailable() + returnBooking.getNumOfSeats());
				flightRepository.save(returnFlight);
			}
		}
		
		BookingPaymentOutput bookingPaymentOutput = new BookingPaymentOutput(); 
		bookingPaymentOutput.setPaymentTxnId(paymentTxn.getId());
		
		return bookingPaymentOutput;
	}

}
