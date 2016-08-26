package com.crossover.airline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.crossover.airline.controller.FlightController;
import com.crossover.airline.resource.output.ErrorOutput;

@ControllerAdvice(assignableTypes = {FlightController.class})
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorOutput handleValidationException(ValidationException e) {
		ErrorOutput errorOutput = getErrorOutput(e);
		
		return errorOutput;
	}

	@ExceptionHandler(AirlineException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorOutput handleAirlineException(ValidationException e) {
		ErrorOutput errorOutput = getErrorOutput(e);
		
		return errorOutput;
	}
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ErrorOutput handleAuthenticationException(AuthenticationException e) {
		ErrorOutput errorOutput = getErrorOutput(e);
		
		return errorOutput;
	}

	private ErrorOutput getErrorOutput(AirlineException e) {
		ErrorOutput errorOutput = new ErrorOutput();
		errorOutput.setErrorCode(e.getErrorCode());
		errorOutput.setMessage(e.getMessage());
		return errorOutput;
	}
}
