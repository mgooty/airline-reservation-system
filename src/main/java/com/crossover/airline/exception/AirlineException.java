package com.crossover.airline.exception;

public class AirlineException extends RuntimeException {

	private static final long serialVersionUID = -8564649319425932206L;
	
	private String errorCode;
	private String message;

	public AirlineException(AirlineError airlineError, Object... args) {
		this.message = String.format(airlineError.getMsg(), args);
		this.errorCode = airlineError.getCode();
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
