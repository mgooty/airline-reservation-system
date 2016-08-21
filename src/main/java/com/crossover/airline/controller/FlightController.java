package com.crossover.airline.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.crossover.airline.entity.FlightClass;
import com.crossover.airline.resource.input.CheckinInput;
import com.crossover.airline.resource.input.CreatePassengersInput;
import com.crossover.airline.resource.input.CreditCardPaymentInput;
import com.crossover.airline.resource.input.FlightBookingInput;
import com.crossover.airline.resource.input.FlightInput;
import com.crossover.airline.resource.output.BookingPaymentOutput;
import com.crossover.airline.resource.output.FlightBookingOutput;
import com.crossover.airline.resource.output.FlightOutput;
import com.crossover.airline.resource.output.FlightSeatOutput;
import com.crossover.airline.resource.output.GetPassengerOutput;
import com.crossover.airline.resource.output.SearchFlightOutput;
import com.crossover.airline.service.FlightService;
import com.crossover.airline.service.PaymentsService;
import com.crossover.airline.validator.FlightValidator;

@RestController
@RequestMapping("/flight")
public class FlightController {

	@Autowired
	private FlightService flightService;
	
	@Autowired
	private PaymentsService paymentService;
	
	@Autowired
	private FlightValidator flightValidator;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public FlightOutput createFlight(@RequestBody FlightInput input) {
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public SearchFlightOutput searchFlights(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, 
											@RequestParam String fromCity, @RequestParam String toCity, @RequestParam FlightClass flightClass, @RequestParam int numOfSeats,
											@RequestParam(required = false, defaultValue = "false") boolean onlyOnward) {
		return flightService.searchFlights(startDate, fromCity, endDate, toCity, flightClass, numOfSeats, onlyOnward);
	}
	
	@RequestMapping(value = "/booking", method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public FlightBookingOutput createBooking(@RequestBody FlightBookingInput flightBookingInput) {
		flightValidator.validateForCreateBooking(flightBookingInput);
		return flightService.createBooking(flightBookingInput);
	}
	
	@RequestMapping(value = "/booking/passengers", method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public GetPassengerOutput createPassengers(@RequestBody CreatePassengersInput createPassengersInput) {
		return flightService.createPassengers(createPassengersInput);
	}
	
	@RequestMapping(value = "/booking/payment/credit", method = RequestMethod.POST)
	public BookingPaymentOutput acceptCreditCardPayment(@RequestBody CreditCardPaymentInput creditCardPaymentInput) throws Exception {
		return paymentService.processCreditCardPayment(creditCardPaymentInput);
	}
	
	@RequestMapping(value = "/booking/{bookingId}/passengers", method = RequestMethod.GET)
	public GetPassengerOutput getPassengers(@PathVariable Long bookingId) {
		return flightService.getPassengers(bookingId);
	}
	
	@RequestMapping(value = "/{flightId}/seats", method = RequestMethod.GET)
	public List<FlightSeatOutput> getFlightSeats(@PathVariable Long flightId) {
		return flightService.getFlightSeats(flightId);
	}
	
	@RequestMapping(value = "/booking/{bookingId}/checkin", method = RequestMethod.POST)
	public void checkin(@PathVariable Long bookingId, @RequestBody CheckinInput checkinInput) {
		flightService.checkIn(bookingId, checkinInput);
	}
	
	@RequestMapping(value = "/booking/my", method = RequestMethod.GET)
	public void myBookings() {
		
	}
	
	@RequestMapping(value = "/booking/{bookingId}/cancel", method = RequestMethod.PUT)
	public void cancelBooking(@PathVariable Long bookingId) {
		
	}
	
	@RequestMapping(value = "/{flightId}/cancel", method = RequestMethod.PUT)
	public void cancelFight(@PathVariable Long flightId) {
		
	}
}
