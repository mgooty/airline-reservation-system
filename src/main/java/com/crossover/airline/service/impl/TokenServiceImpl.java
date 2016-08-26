package com.crossover.airline.service.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.crossover.airline.auth.JWTPayload;
import com.crossover.airline.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenServiceImpl implements TokenService {

	@Value("${security.token.expiration.time.minutes:60}")
	private Integer expirationTimeInMins;
	
	@Value("${security.token.secret:asdfasdfasdf}")
	private String tokenSecret;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public String createToken(String email) throws Exception {
		JWTPayload jwtPayload = new JWTPayload();
		jwtPayload.setEmail(email);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, expirationTimeInMins);
		jwtPayload.setExp(cal.getTime().getTime());
		
		String payload = objectMapper.writeValueAsString(jwtPayload);
		
		return Jwts.builder().setPayload(payload).signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes()).compact();
	}

}
