package com.crossover.airline.controller;

import java.util.Date;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.Flight.FlightStatus;
import com.crossover.airline.entity.FlightClass;
import com.crossover.airline.repository.FlightRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
public class FlightControllerTest { // extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private FlightRepository flightRepository;
	
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	@Rollback
	@Sql(scripts = {"/setup.sql"})
	public void createBooking() throws Exception {
		Flight flight = new Flight();
		flight.setDepartureDate(new Date());
		flight.setDurationInMins(100);
		flight.setFlightClass(FlightClass.BUSINESS);
		flight.setFlightCode("1234");
		flight.setFlightStatus(FlightStatus.SCHEDULED);
		flight.setFromCity("from city");
		flight.setLockId(1);
		flight.setTotalNoOfSeats(10);
		flight.setNoOfSeatsAvailable(10);
		flight.setPricePerSeat(100.0);
		flight.setToCity("to city");
		
		flightRepository.save(flight);
		
		MvcResult result = mockMvc.perform(get("/flight?startDate=2016-08-17&endDate=2016-08-17&fromCity=BANGALORE&toCity=DELHI&flightClass=ECONOMY&numOfSeats=2&onlyOnward=false"))
				.andExpect(status().isOk())
				.andReturn();
		
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void findFlights() {
		System.out.println("All the flights =====> " + flightRepository.findAll().size());
	}
}
