package com.crossover.airline.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.PaymentTxn;
import com.crossover.airline.entity.Booking.BookingStatus;
import com.crossover.airline.entity.PaymentTxn.PaymentMode;
import com.crossover.airline.entity.PaymentTxn.PaymentStatus;
import com.crossover.airline.repository.BookingRepository;
import com.crossover.airline.repository.FlightRepository;
import com.crossover.airline.repository.PaymentTxnRepository;
import com.crossover.airline.resource.input.CreditCardPaymentInput;
import com.crossover.airline.resource.output.BookingPaymentOutput;
import com.crossover.airline.service.DocumentService;
import com.crossover.airline.service.MailService;
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
	private DocumentService documentService;
	
	@Autowired
	private MailService mailService;
	
	private String subject = "e-ticket for your upcoming journey";
	
	private String body = "Hi %s, \n Please find attached your e-tickets \n Regards, \nAirline Reservation System";
	
	@Override
	public BookingPaymentOutput processCreditCardPayment(CreditCardPaymentInput creditCardPaymentInput) throws Exception {
		// TODO: integrate with credit card processing systems
		Booking onwardBooking = bookingRepository.findOne(creditCardPaymentInput.getOnwardBookingId());
		Booking returnBooking = null;
		
		if(creditCardPaymentInput.getReturnBookingId() != null && creditCardPaymentInput.getReturnBookingId() != 0) {
			returnBooking = bookingRepository.findOne(creditCardPaymentInput.getOnwardBookingId());
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
				
				returnFlight = flightRepository.findOne(onwardBooking.getFlight().getId());
				returnFlight.setNoOfSeatsBooked(returnFlight.getNoOfSeatsBooked() + returnBooking.getNumOfSeats());
				flightRepository.save(returnFlight);
			}
			
			// TODO: push an event to the queue - PAYMENT_PROCESSED. Event handler will email tickets as PDF document
			String pathToTicket = ""; 
			if(returnBooking == null) {
				documentService.generateTicketAsPdf(onwardBooking.getId(), paymentTxn.getId(), onwardFlight.getId());
				mailService.sendMail(subject, onwardBooking.getEmail(), String.format(body, onwardBooking.getName()), pathToTicket);
			} else {
				documentService.generateTicketAsPdf(onwardBooking.getId(), returnBooking.getId(), paymentTxn.getId(), onwardFlight.getId(), returnFlight.getId());
				mailService.sendMail(subject, onwardBooking.getEmail(), String.format(body, onwardBooking.getName()), pathToTicket);
			}
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
