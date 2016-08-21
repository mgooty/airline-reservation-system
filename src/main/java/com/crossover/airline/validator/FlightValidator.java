package com.crossover.airline.validator;

import com.crossover.airline.resource.input.FlightBookingInput;

public interface FlightValidator {

	public void validateForCreateBooking(FlightBookingInput flightBookingInput);
}
