package com.crossover.airline.service.impl;

import static com.crossover.airline.common.Constants.PAYMENT_PROCESSED_QUEUE_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Booking.BookingStatus;
import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.PaymentTxn;
import com.crossover.airline.entity.PaymentTxn.PaymentMode;
import com.crossover.airline.entity.PaymentTxn.PaymentStatus;
import com.crossover.airline.event.PaymentProcessedEvent;
import com.crossover.airline.ext.payment.gateway.PaymentGatewayInput;
import com.crossover.airline.ext.payment.gateway.PaymentGatewayOutput;
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
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${payment.gateway.api.url}")
	private String paymentGatewayUrl;
	
	@Override
	public BookingPaymentOutput processCreditCardPayment(CreditCardPaymentInput creditCardPaymentInput) {
		Booking onwardBooking = bookingRepository.findOne(creditCardPaymentInput.getOnwardBookingId());
		Booking returnBooking = null;
		
		if(creditCardPaymentInput.getReturnBookingId() != null && creditCardPaymentInput.getReturnBookingId() != 0) {
			returnBooking = bookingRepository.findOne(creditCardPaymentInput.getReturnBookingId());
		}
		
		Double transactionAmt = onwardBooking.getAmount();
		if(returnBooking != null) {
			transactionAmt = transactionAmt + returnBooking.getAmount();
		}
		
		PaymentGatewayOutput paymentGatewayOutput = makePayment(transactionAmt, creditCardPaymentInput);
		PaymentTxn paymentTxn = createPaymentTxn(onwardBooking, returnBooking, paymentGatewayOutput);
		
		if(paymentTxn.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
			handleSuccessPaymentTxn(onwardBooking, returnBooking, paymentTxn);
		} else {
			handleFailedPaymentTxn(onwardBooking, returnBooking);
		}
		
		BookingPaymentOutput bookingPaymentOutput = new BookingPaymentOutput(); 
		bookingPaymentOutput.setPaymentTxnId(paymentTxn.getId());
		bookingPaymentOutput.setStatus(paymentTxn.getPaymentStatus());
		
		return bookingPaymentOutput;
	}

	private void handleFailedPaymentTxn(Booking onwardBooking, Booking returnBooking) {
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

	private void handleSuccessPaymentTxn(Booking onwardBooking, Booking returnBooking, PaymentTxn paymentTxn) {
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
		
		PaymentProcessedEvent event = null;
		if(returnBooking == null) {
			event = new PaymentProcessedEvent(onwardBooking.getId(), paymentTxn.getId(), onwardFlight.getId());
		} else {
			event = new PaymentProcessedEvent(onwardFlight.getId(), onwardBooking.getId(), returnFlight.getId(), returnBooking.getId(), paymentTxn.getId());
		}
		jmsTemplate.convertAndSend(PAYMENT_PROCESSED_QUEUE_NAME, event);
	}

	private PaymentTxn createPaymentTxn(Booking onwardBooking, Booking returnBooking, PaymentGatewayOutput paymentGatewayOutput) {
		PaymentTxn paymentTxn = new PaymentTxn();
		paymentTxn.setOnwardBooking(onwardBooking);
		paymentTxn.setReturnBooking(returnBooking != null ? returnBooking : null);
		paymentTxn.setPaymentMode(PaymentMode.CREDIT_CARD);
		paymentTxn.setPaymentStatus(paymentGatewayOutput.getStatus());
		paymentTxn.setTxnRefNumber(paymentGatewayOutput.getReferenceNum());
		
		return paymentTxnRepository.save(paymentTxn);
	}

	private PaymentGatewayOutput makePayment(Double transactionAmt, CreditCardPaymentInput creditCardPaymentInput) {
		PaymentGatewayInput input = new PaymentGatewayInput();
		input.setTransactionAmt(transactionAmt);
		
		return restTemplate.postForEntity(paymentGatewayUrl, input, PaymentGatewayOutput.class).getBody();
	}
}
