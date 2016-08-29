package com.crossover.airline.repository;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.crossover.airline.BaseRepositoryTest;
import com.crossover.airline.entity.User;

@Transactional
public class UserRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Test
	@Sql(value = {"/user.sql"})
	public void testFindByEmailAndPassword() {
		User user = userRepository.findByEmailAndPassword("mithun.gooty@gmail.com", "password");
		
		assertEquals("mithun.gooty@gmail.com", user.getEmail());
		assertEquals("password", user.getPassword());
	}
	
}
