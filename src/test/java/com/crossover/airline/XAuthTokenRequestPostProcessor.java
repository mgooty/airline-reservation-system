package com.crossover.airline;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.StringUtils;

public class XAuthTokenRequestPostProcessor implements RequestPostProcessor {

	private String token;

	@Override
	public MockHttpServletRequest postProcessRequest(MockHttpServletRequest mockRequest) {
		if(StringUtils.hasText(token)) {
			mockRequest.addHeader("X-AUTH-TOKEN", token);
		}
		return mockRequest;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}