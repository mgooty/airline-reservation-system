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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.crossover.airline.common.Constants;
import com.crossover.airline.entity.User;
import com.crossover.airline.exception.AirlineError;
import com.crossover.airline.exception.AuthenticationException;
import com.crossover.airline.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultJwtParser;

public class StatelessAuthenticationFilter extends GenericFilterBean {

	@Value("${security.token.secret:asdfasdfasdf}")
	private String tokenSecret;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
							throws IOException, ServletException {
		System.out.println("stateless authentication filter");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

//		try {
			
			String token = httpRequest.getHeader(Constants.X_AUTH_TOKEN_HEADER_NAME);
			if(!StringUtils.hasText(token)) {
				throw new AuthenticationException(AirlineError.AUTHENTICATION_AUTH_TOKEN_MISSING);
			}
			
			JWTPayload jwtPayload = new JWTPayload();
			byte[] secret = tokenSecret.getBytes();
			DefaultJwtParser defaultJwtParser = new DefaultJwtParser();
			defaultJwtParser.setSigningKey(secret);
	
			Claims claims = defaultJwtParser.parseClaimsJws(token).getBody();
			jwtPayload.setEmail((String) claims.get("email"));
			jwtPayload.setExp((Long) claims.get("exp"));
			
			if (new DateTime(jwtPayload.getExp()).isBeforeNow()) {
				throw new AuthenticationException(AirlineError.AUTHENTICATION_AUTH_TOKEN_EXPIRED);
			}
			
			User user = userRepository.findOne(jwtPayload.getEmail());
			SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user.getEmail()));
			chain.doFilter(request, response);
//		} catch(Exception e) {
//			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//		}
	}

}
