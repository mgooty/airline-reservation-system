package com.crossover.airline.repository;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.crossover.airline.BaseRepositoryTest;
import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.FlightClass;

@Transactional
public class FlightRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private FlightRepository flightRepository;
	
	@Test
	@Sql(value = {"/setup.sql", "/flight.sql"})
	public void testSearchFlights() {
		GregorianCalendar startDate = new GregorianCalendar(2016, 7, 17);
		GregorianCalendar endDate = new GregorianCalendar(2016, 7, 17);
		List<Flight> flights = flightRepository.searchFlights(startDate.getTime(), "BANGALORE", endDate.getTime(), "DELHI", FlightClass.ECONOMY, 2);
		
		assertEquals(2, flights.size());
	}
}
