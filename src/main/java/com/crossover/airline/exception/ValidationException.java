package com.crossover.airline.exception;

public class ValidationException extends AirlineException {

	private static final long serialVersionUID = -2439654961773059163L;
	
	public ValidationException(AirlineError airlineError, Object... args) {
		super(airlineError, args);
	}

}
