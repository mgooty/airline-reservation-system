package com.crossover.airline.service;

import java.util.Date;
import java.util.List;

import com.crossover.airline.entity.FlightClass;
import com.crossover.airline.resource.input.CheckinInput;
import com.crossover.airline.resource.input.CreatePassengersInput;
import com.crossover.airline.resource.input.FlightBookingInput;
import com.crossover.airline.resource.output.FlightBookingOutput;
import com.crossover.airline.resource.output.FlightSeatOutput;
import com.crossover.airline.resource.output.GetPassengerOutput;
import com.crossover.airline.resource.output.SearchFlightOutput;

public interface FlightService {

	SearchFlightOutput searchFlights(Date departureDate, String fromCity, Date returnDate, String toCity, FlightClass flightClass, int numOfSeats, boolean onlyOnwards);

	FlightBookingOutput createBooking(FlightBookingInput flightBookingInput);

	GetPassengerOutput getPassengers(Long bookingId);

	List<FlightSeatOutput> getFlightSeats(Long flightId);

	void checkIn(Long bookingId, CheckinInput checkinInput);

	GetPassengerOutput createPassengers(CreatePassengersInput createPassengersInput);

}
