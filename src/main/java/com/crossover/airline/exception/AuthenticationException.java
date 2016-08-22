package com.crossover.airline.exception;

public class AuthenticationException extends AirlineException {

	private static final long serialVersionUID = 401353538318727668L;

	public AuthenticationException(AirlineError airlineError, Object... args) {
		super(airlineError, args);
	}

}
