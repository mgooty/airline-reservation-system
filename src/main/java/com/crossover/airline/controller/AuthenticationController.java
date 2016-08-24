package com.crossover.airline.controller;

import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crossover.airline.auth.JWTPayload;
import com.crossover.airline.common.Constants;
import com.crossover.airline.entity.User;
import com.crossover.airline.exception.AirlineError;
import com.crossover.airline.exception.AuthenticationException;
import com.crossover.airline.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private UserRepository userRepository;
	
	@Value("${security.token.expiration.time.minutes:60}")
	private Integer expirationTimeInMins;
	
	@Value("${security.token.secret:asdfasdfasdf}")
	private String tokenSecret;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@RequestMapping
	public void authenticateInternalUsers(@RequestParam String email, @RequestParam String password, HttpServletResponse response) throws Exception {
		User user = userRepository.findByEmailAndPassword(email, password);
		
		if(user == null) {
			throw new AuthenticationException(AirlineError.AUTHENTICATION_ERROR);
		}
		
		JWTPayload jwtPayload = new JWTPayload();
		jwtPayload.setEmail(user.getEmail());
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, expirationTimeInMins);
		jwtPayload.setExp(cal.getTime().getTime());
		
		String payload = objectMapper.writeValueAsString(jwtPayload);
		
		String jwtToken = Jwts.builder().setPayload(payload).signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes()).compact();
		
		response.addHeader(Constants.X_AUTH_TOKEN_HEADER_NAME, jwtToken);
	}
}
