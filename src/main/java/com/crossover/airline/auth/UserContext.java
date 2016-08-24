package com.crossover.airline.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

	
	public String getCurrentUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
