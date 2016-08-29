package com.crossover.airline.service;

import static com.crossover.airline.common.Constants.PAYMENT_PROCESSED_QUEUE_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;

import com.crossover.airline.BaseServiceTest;
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
import com.crossover.airline.service.impl.PaymentsServiceImpl;

public class PaymentServiceTest extends BaseServiceTest {

	@InjectMocks
	PaymentsService unit = new PaymentsServiceImpl();
	
	@Mock
	private BookingRepository bookingRepository;
	
	@Mock
	private PaymentTxnRepository paymentTxnRepository;
	
	@Mock
	private FlightRepository flightRepository;
	
	@Mock
	private JmsTemplate jmsTemplate;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Test
	public void testIfCreditCardPaymentIsProcessedSuccessfully() {
		// Given
		Long onwardBookingId = 12345l;
		Long returnBookingId = 67890l;
		Long onwardFlightId = 564342l;
		Long returnFlightId = 938402l;
		Long paymentTxnId = 97493l;
		
		Flight onwardFlight = new Flight();
		onwardFlight.setId(onwardFlightId);
		
		Flight returnFlight = new Flight();
		returnFlight.setId(returnFlightId);
		
		Booking onwardBooking = new Booking();
		onwardBooking.setId(onwardBookingId);
		onwardBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		onwardBooking.setFlight(onwardFlight);
		when(bookingRepository.findOne(onwardBookingId)).thenReturn(onwardBooking);
		
		Booking returnBooking = new Booking();
		returnBooking.setId(returnBookingId);
		returnBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		returnBooking.setFlight(returnFlight);
		when(bookingRepository.findOne(returnBookingId)).thenReturn(returnBooking);
		
		when(flightRepository.findOne(onwardFlightId)).thenReturn(onwardFlight);
		when(flightRepository.findOne(returnFlightId)).thenReturn(returnFlight);
		
		PaymentTxn paymentTxn = new PaymentTxn();
		paymentTxn.setId(paymentTxnId);
		paymentTxn.setOnwardBooking(onwardBooking);
		paymentTxn.setReturnBooking(returnBooking);
		paymentTxn.setPaymentMode(PaymentMode.CREDIT_CARD);
		paymentTxn.setPaymentStatus(PaymentStatus.SUCCESS);
		when(paymentTxnRepository.save(paymentTxn)).thenReturn(paymentTxn);
		
		PaymentProcessedEvent event = new PaymentProcessedEvent(onwardFlight.getId(), onwardBooking.getId(), returnFlight.getId(), returnBooking.getId(), paymentTxn.getId());
		
		setRestTemplateBehaviour(PaymentStatus.SUCCESS, "TXN_REF_SUCCESS");
		
		// When
		CreditCardPaymentInput input = new CreditCardPaymentInput();
		input.setCreditCardNumber("1234567890123456");
		input.setName("test user");
		input.setExpiryDate("");
		input.setCvvNumber("1234");
		input.setOnwardBookingId(onwardBookingId);
		input.setReturnBookingId(returnBookingId);
		
		BookingPaymentOutput output = unit.processCreditCardPayment(input);
		
		// Then
		assertEquals(paymentTxnId, output.getPaymentTxnId());
		assertEquals(PaymentStatus.SUCCESS, output.getStatus());
		
		verify(bookingRepository).findOne(onwardBookingId);
		verify(bookingRepository).findOne(returnBookingId);
		verify(flightRepository).findOne(onwardFlightId);
		verify(flightRepository).findOne(returnFlightId);
		verify(paymentTxnRepository).save(paymentTxn);
		
		onwardBooking.setBookingStatus(BookingStatus.CONFIRMED);
		verify(bookingRepository).save(onwardBooking);
		
		onwardFlight.setNoOfSeatsBooked(onwardFlight.getNoOfSeatsBooked() + onwardBooking.getNumOfSeats());
		verify(flightRepository).save(onwardFlight);
		
		returnBooking.setBookingStatus(BookingStatus.CONFIRMED);
		verify(bookingRepository).save(returnBooking);
		
		returnFlight.setNoOfSeatsBooked(returnFlight.getNoOfSeatsBooked() + returnBooking.getNumOfSeats());
		verify(flightRepository).save(returnFlight);
		
		verify(jmsTemplate).convertAndSend(PAYMENT_PROCESSED_QUEUE_NAME, event);
		
		verifyNoMoreInteractions(bookingRepository, paymentTxnRepository, flightRepository, jmsTemplate);
	}
	
	@Test
	public void testIfCreditCardPaymentFailureIsHandled() {
		// Given
		Long onwardBookingId = 12345l;
		Long returnBookingId = 67890l;
		Long onwardFlightId = 564342l;
		Long returnFlightId = 938402l;
		Long paymentTxnId = 97493l;
		
		Flight onwardFlight = new Flight();
		onwardFlight.setId(onwardFlightId);
		
		Flight returnFlight = new Flight();
		returnFlight.setId(returnFlightId);
		
		Booking onwardBooking = new Booking();
		onwardBooking.setId(onwardBookingId);
		onwardBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		onwardBooking.setFlight(onwardFlight);
		when(bookingRepository.findOne(onwardBookingId)).thenReturn(onwardBooking);
		
		Booking returnBooking = new Booking();
		returnBooking.setId(returnBookingId);
		returnBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		returnBooking.setFlight(returnFlight);
		when(bookingRepository.findOne(returnBookingId)).thenReturn(returnBooking);
		
		when(flightRepository.findOne(onwardFlightId)).thenReturn(onwardFlight);
		when(flightRepository.findOne(returnFlightId)).thenReturn(returnFlight);
		
		PaymentTxn paymentTxn = new PaymentTxn();
		paymentTxn.setId(paymentTxnId);
		paymentTxn.setOnwardBooking(onwardBooking);
		paymentTxn.setReturnBooking(returnBooking);
		paymentTxn.setPaymentMode(PaymentMode.CREDIT_CARD);
		paymentTxn.setPaymentStatus(PaymentStatus.FAILED);
		when(paymentTxnRepository.save(paymentTxn)).thenReturn(paymentTxn);
		
		setRestTemplateBehaviour(PaymentStatus.FAILED, "TXN_REF_FAILED");
		
		// When
		CreditCardPaymentInput input = new CreditCardPaymentInput();
		input.setCreditCardNumber("1234567890123456");
		input.setName("test user");
		input.setExpiryDate("");
		input.setCvvNumber("1234");
		input.setOnwardBookingId(onwardBookingId);
		input.setReturnBookingId(returnBookingId);
		
		BookingPaymentOutput output = unit.processCreditCardPayment(input);
		
		// Then
		assertEquals(paymentTxnId, output.getPaymentTxnId());
		assertEquals(PaymentStatus.FAILED, output.getStatus());
		
		verify(bookingRepository).findOne(onwardBookingId);
		verify(bookingRepository).findOne(returnBookingId);
		verify(flightRepository).findOne(onwardFlightId);
		verify(flightRepository).findOne(returnFlightId);
		verify(paymentTxnRepository).save(paymentTxn);
		
		onwardBooking.setBookingStatus(BookingStatus.PAYMENT_FAILED);
		verify(bookingRepository).save(onwardBooking);
		
		onwardFlight.setNoOfSeatsBooked(onwardFlight.getNoOfSeatsAvailable() + onwardBooking.getNumOfSeats());
		verify(flightRepository).save(onwardFlight);
		
		returnBooking.setBookingStatus(BookingStatus.PAYMENT_FAILED);
		verify(bookingRepository).save(returnBooking);
		
		returnFlight.setNoOfSeatsBooked(returnFlight.getNoOfSeatsAvailable() + returnBooking.getNumOfSeats());
		verify(flightRepository).save(returnFlight);
		
		verifyNoMoreInteractions(bookingRepository, paymentTxnRepository, flightRepository, jmsTemplate);
	}
	
	private void setRestTemplateBehaviour(PaymentStatus status, String referenceNum) {
		
		PaymentGatewayOutput output = new PaymentGatewayOutput();
		output.setStatus(status);
		output.setReferenceNum(referenceNum);
		
		ResponseEntity<PaymentGatewayOutput> responseEntity = new ResponseEntity<>(output, HttpStatus.OK); 
		when(restTemplate.postForEntity(any(String.class), any(PaymentGatewayInput.class), Mockito.<Class<PaymentGatewayOutput>> any()))
						.thenReturn(responseEntity);
	}
}
