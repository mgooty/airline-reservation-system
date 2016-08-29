package com.crossover.airline.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import com.crossover.airline.BaseControllerTest;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class AuthenticationControllerTest extends BaseControllerTest {

	@Test
	@Sql(scripts = {"/setup.sql", "/user.sql"})
	public void testIfUserIsAuthenticatedSuccessfully() throws Exception {
		
		MvcResult result = mockMvc.perform(post("/auth").param("email", "mithun.gooty@gmail.com").param("password", "password"))
							.andExpect(status().isOk())
							.andExpect(header().string("X-AUTH-TOKEN", notNullValue()))
							.andReturn();
		
		System.out.println();
	}
}
