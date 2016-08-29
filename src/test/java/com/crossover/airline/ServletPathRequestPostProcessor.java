package com.crossover.airline;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@Component
public class ServletPathRequestPostProcessor implements RequestPostProcessor {

	private String servletPath;

	@Override
	public MockHttpServletRequest postProcessRequest(MockHttpServletRequest mockRequest) {
		mockRequest.setServletPath(servletPath);
		return mockRequest;
	}

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}
}
