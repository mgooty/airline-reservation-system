package com.crossover.airline.exception;

public enum AirlineError {

	CREATE_FLIGHT_BOOKING_NUM_OF_SEATS_IS_MANDATORY("CFB001", "Number of seats is mandatory to create a booking"),
	CREATE_FLIGHT_BOOKING_AMOUNT_IS_MANDATORY("CFB002", "Flight booking amount is mandatory"),
	CREATE_FLIGHT_EMAILID_IS_MANDATORY("CFB003", "eamil ID is mandatory to book a flight"),
	CREATE_FLIGHT_NAME_IS_MANDATORY("CFB004", "Name is mandatory to book a flight"),
	CREATE_FLIGHT_BOOKING_PASSENDER_LIST_IS_EMPTY("CFB005", "Passenger details cannot be empty to book a flight"),
	CREATE_FLIGHT_BOOKING_MOBILE_NUM_IS_MANDATORY("CFB006", "Mobile number is mandatory to book a flight"),
	CREATE_FLIGHT_RETURN_BOOKING_INSUFFICIENT_SEATS("CFB007", "We do not have %s seats available for your return journey. Only %s seats are available for booking"),
	CREATE_FLIGHT_ONWARD_BOOKING_INSUFFICIENT_SEATS("CFB008", "We do not have %s seats available for your onward journey. Only %s seats are available for booking"),
	CREATE_FLIGHT_BOOKING_NUM_OF_PASSENGERS_MISMATCH("CFB009", "Number of seats booked is %s, but passenger details provided for %s people"),
	CREATE_FLIGHT_ONWARD_FLIGHTID_IS_EMPTY("CFB010", "Onward flight ID cannot be empty"),
	
	FLIGHT_CHECKIN_SEAT_NOT_AVAILABLE("FCE001", "Seat number %s is not available anymore"),
	FLIGHT_CHECKIN_NOT_ALLOWED("FCE002", "You can check-in only %s Hrs within the departure time"),
	
	AUTHENTICATION_ERROR("AE001", "Invalid email and password combination"),
	AUTHENTICATION_AUTH_TOKEN_MISSING("AE002", "X-AUTH-TOKEN header is mandatory"),
	AUTHENTICATION_AUTH_TOKEN_EXPIRED("AE00", "X-AUTH-TOKEN header has expired")
	;
	
	private final String code;
	private final String msg;

	private AirlineError(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
