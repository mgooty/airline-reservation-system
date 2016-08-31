package com.crossover.airline.validator.impl;

import static com.crossover.airline.exception.AirlineError.CREATE_FLIGHT_BOOKING_MOBILE_NUM_IS_MANDATORY;
import static com.crossover.airline.exception.AirlineError.CREATE_FLIGHT_BOOKING_NUM_OF_SEATS_IS_MANDATORY;
import static com.crossover.airline.exception.AirlineError.CREATE_FLIGHT_ONWARD_FLIGHTID_IS_EMPTY;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.crossover.airline.exception.ValidationException;
import com.crossover.airline.resource.input.FlightBookingInput;
import com.crossover.airline.validator.FlightValidator;

@Component
public class FlightValidatorImpl implements FlightValidator {

	@Override
	public void validateForCreateBooking(FlightBookingInput flightBookingInput) {
		if(flightBookingInput.getNumOfSeats() == null || flightBookingInput.getNumOfSeats() == 0) {
			throw new ValidationException(CREATE_FLIGHT_BOOKING_NUM_OF_SEATS_IS_MANDATORY);
		}
		
		if(flightBookingInput.getOnwardFlightId() == null || flightBookingInput.getOnwardFlightId() == 0) {
			throw new ValidationException(CREATE_FLIGHT_ONWARD_FLIGHTID_IS_EMPTY);
		}
	}

}
