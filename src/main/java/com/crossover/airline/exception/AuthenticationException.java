package com.crossover.airline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends AirlineException {

	private static final long serialVersionUID = 401353538318727668L;

	public AuthenticationException(AirlineError airlineError, Object... args) {
		super(airlineError, args);
	}

}
