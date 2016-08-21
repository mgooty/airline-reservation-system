package com.crossover.airline.resource.output;

import com.crossover.airline.resource.BaseOutput;

public class ErrorOutput extends BaseOutput {

	private static final long serialVersionUID = -7837740241197910282L;
	
	private String errorCode;
	private String message;
	private String detailedErrorMessage;
	
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

	public String getDetailedErrorMessage() {
		return detailedErrorMessage;
	}

	public void setDetailedErrorMessage(String detailedErrorMessage) {
		this.detailedErrorMessage = detailedErrorMessage;
	}
}
