package com.crossover.airline.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import javax.servlet.Filter;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.crossover.airline.entity.User;
import com.crossover.airline.repository.UserRepository;
import com.crossover.airline.service.TokenService;

public abstract class BaseControllerTest {

	@Autowired
	private WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Autowired
	private Filter springSecurityFilterChain;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UserRepository userRepository;
	
	protected XAuthTokenRequestPostProcessor xAuthToken = new XAuthTokenRequestPostProcessor();
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).defaultRequest(get("/").with(xAuthToken)
												.contentType(MediaType.APPLICATION_JSON))
												.addFilters(springSecurityFilterChain).build();
		
		User user = createUser();
		
		String xauthToken = tokenService.createToken(user.getEmail());
		xAuthToken.setToken(xauthToken);
	}

	private User createUser() {
		User user = new User();
		user.setEmail("test@abc.com");
		user.setPassword("password");
		
		return userRepository.save(user);
	}
	
	
}
