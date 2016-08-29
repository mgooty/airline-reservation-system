package com.crossover.airline;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import javax.servlet.Filter;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.crossover.airline.entity.User;
import com.crossover.airline.entity.User.Role;
import com.crossover.airline.repository.UserRepository;
import com.crossover.airline.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseControllerTest {

	@Autowired
	private WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Autowired
	private Filter springSecurityFilterChain;
	
	@Autowired
	protected TokenService tokenService;
	
	@Autowired
	private UserRepository userRepository;
	
	protected XAuthTokenRequestPostProcessor xAuthToken = new XAuthTokenRequestPostProcessor();
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).defaultRequest(get("/").with(xAuthToken)
												.contentType(MediaType.APPLICATION_JSON))
												.addFilters(springSecurityFilterChain).build();
		
//		User user = createInternalUser();
//		
//		String xauthToken = tokenService.createToken(user.getEmail());
//		xAuthToken.setToken(xauthToken);
	}

	protected User createInternalUser() {
		User user = new User();
		user.setEmail("internal@abc.com");
		user.setPassword("password");
		user.setName("internal user");
		user.setRole(Role.ROLE_INTERNAL);
		
		return userRepository.save(user);
	}
	
	protected User createPublicUser() {
		User user = new User();
		user.setEmail("public@abc.com");
		user.setPassword("password");
		user.setName("public user");
		user.setRole(Role.ROLE_PUBLIC);
		
		return userRepository.save(user);
	}
	
	public static String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}
}
