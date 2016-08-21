package com.crossover.airline.controller;

import java.util.Date;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.FlightClass;
import com.crossover.airline.repository.FlightRepository;
import com.crossover.airline.service.FlightService;
import com.crossover.airline.service.PaymentsService;
import com.crossover.airline.validator.FlightValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {FlightController.class})
public class FlightControllerTest {

	@MockBean
	private FlightService flightService;
	
	@MockBean
	private PaymentsService paymentService;
	
	@MockBean
	private FlightValidator flightValidator;
	
	@Test
	public void searchFlights() {
		
	}
}
