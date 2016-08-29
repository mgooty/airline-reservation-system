package com.crossover.airline.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.crossover.airline.common.Constants;
import com.crossover.airline.entity.User;
import com.crossover.airline.exception.AirlineError;
import com.crossover.airline.exception.AuthenticationException;
import com.crossover.airline.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultJwtParser;

@Component
public class StatelessAuthenticationFilter extends GenericFilterBean {

	@Value("${security.token.secret:asdfasdfasdf}")
	private String tokenSecret;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
							throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String token = httpRequest.getHeader(Constants.X_AUTH_TOKEN_HEADER_NAME);
		if(!StringUtils.hasText(token)) {
			throw new AuthenticationException(AirlineError.AUTHENTICATION_AUTH_TOKEN_MISSING);
		}
		
		JWTPayload jwtPayload = new JWTPayload();
		try {
			byte[] secret = tokenSecret.getBytes();
			DefaultJwtParser defaultJwtParser = new DefaultJwtParser();
			defaultJwtParser.setSigningKey(secret);
	
			Claims claims = defaultJwtParser.parseClaimsJws(token).getBody();
			jwtPayload.setEmail((String) claims.get("email"));
			jwtPayload.setExp((Long) claims.get("exp"));
		} catch(Exception e) {
			e.printStackTrace();
			handleAuthenticationFailure(httpResponse, AirlineError.AUTHENTICATION_AUTH_TOKEN_INVALID.getMsg());
			return;
		}
		
		if (new DateTime(jwtPayload.getExp()).isBeforeNow()) {
			handleAuthenticationFailure(httpResponse, AirlineError.AUTHENTICATION_AUTH_TOKEN_EXPIRED.getMsg());
			return;
		}
		
		User user = userRepository.findOne(jwtPayload.getEmail());
		if(user == null) {
			handleAuthenticationFailure(httpResponse, "User does not exist in the system");
			return;
		}
		
		SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user.getEmail(), user.getName(), user.getRole()));
		chain.doFilter(request, response);
	}

	private void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		response.getOutputStream().print("{\"Error\":" + "\"" + message + "\"}");
	}
}
