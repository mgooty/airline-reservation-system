package com.crossover.airline.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crossover.airline.common.Constants;
import com.crossover.airline.entity.User;
import com.crossover.airline.exception.AirlineError;
import com.crossover.airline.exception.AuthenticationException;
import com.crossover.airline.repository.UserRepository;
import com.crossover.airline.service.TokenService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TokenService tokenService;
	
	@RequestMapping
	public void authenticateInternalUsers(@RequestParam String email, @RequestParam String password, HttpServletResponse response) throws Exception {
		User user = userRepository.findByEmailAndPassword(email, password);
		
		if(user == null) {
			throw new AuthenticationException(AirlineError.AUTHENTICATION_ERROR);
		}
		
		String jwtToken = tokenService.createToken(user.getEmail());
		
		response.addHeader(Constants.X_AUTH_TOKEN_HEADER_NAME, jwtToken);
	}
}
