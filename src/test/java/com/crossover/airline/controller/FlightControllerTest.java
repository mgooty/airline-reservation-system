package com.crossover.airline.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import com.crossover.airline.BaseControllerTest;
import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Passenger.Gender;
import com.crossover.airline.entity.PaymentTxn.PaymentStatus;
import com.crossover.airline.ext.payment.gateway.PaymentGatewayInput;
import com.crossover.airline.ext.payment.gateway.PaymentGatewayOutput;
import com.crossover.airline.entity.User;
import com.crossover.airline.repository.BookingRepository;
import com.crossover.airline.repository.FlightRepository;
import com.crossover.airline.resource.input.CreatePassengersInput;
import com.crossover.airline.resource.input.CreatePassengersInput.BookingPassengerRecord;
import com.crossover.airline.resource.input.CreditCardPaymentInput;
import com.crossover.airline.resource.input.FlightBookingInput;
import com.crossover.airline.resource.input.PassengerInput;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class FlightControllerTest extends BaseControllerTest {
	
	@Autowired
	BookingRepository bookingRepository;
	
	@MockBean
	JmsTemplate jmsTemplate;
	
	@MockBean
	RestTemplate restTemplate;
	
	@Autowired
	FlightRepository flightRepository;
	
	@Test
	@Sql(scripts = {"/setup.sql", "/user.sql", "/flight.sql", "/flight_seat.sql"})
	public void testEndToEnd() throws Exception {
		User user = createPublicUser();
		String xauthToken = tokenService.createToken(user.getEmail());
		xAuthToken.setToken(xauthToken);
		
		// Search flight
		MvcResult result = mockMvc.perform(get("/flight")
						.param("startDate", "2016-08-17")
						.param("endDate", "2016-08-17")
						.param("fromCity", "BANGALORE")
						.param("toCity", "DELHI")
						.param("flightClass", "ECONOMY")
						.param("numOfSeats", "2")
						.param("onlyOnward", "false"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.onwardFlights").isArray())
				.andExpect(jsonPath("$.returnFlights").isArray())
				.andExpect(jsonPath("$.onwardFlights[0].flightCode").value("F001"))
				.andExpect(jsonPath("$.onwardFlights[0].fromCity").value("BANGALORE"))
				.andExpect(jsonPath("$.onwardFlights[0].toCity").value("DELHI"))
				.andExpect(jsonPath("$.onwardFlights[0].durationInMins").value(120))
				.andExpect(jsonPath("$.onwardFlights[0].flightStatus").value("SCHEDULED"))
				.andExpect(jsonPath("$.onwardFlights[0].flightClass").value("ECONOMY"))
				.andExpect(jsonPath("$.onwardFlights[0].pricePerSeat").value(250.0))
				.andExpect(jsonPath("$.onwardFlights[0].departureDate").value(1471383000000l))
				.andExpect(jsonPath("$.returnFlights[0].flightCode").value("F003"))
				.andExpect(jsonPath("$.returnFlights[0].fromCity").value("DELHI"))
				.andExpect(jsonPath("$.returnFlights[0].toCity").value("BANGALORE"))
				.andExpect(jsonPath("$.returnFlights[0].durationInMins").value(60))
				.andExpect(jsonPath("$.returnFlights[0].flightStatus").value("SCHEDULED"))
				.andExpect(jsonPath("$.returnFlights[0].flightClass").value("ECONOMY"))
				.andExpect(jsonPath("$.returnFlights[0].pricePerSeat").value(200.0))
				.andExpect(jsonPath("$.returnFlights[0].departureDate").value(1471390200000l))
				.andReturn();
		
		String searchResponse = result.getResponse().getContentAsString();
		Long onwardFlightId = Long.valueOf(JsonPath.read(searchResponse, "$.onwardFlights[0].id") + "");
		Long returnFlightId = Long.valueOf(JsonPath.read(searchResponse, "$.returnFlights[0].id") + "");
		
		// Create booking
		FlightBookingInput bookingInput = new FlightBookingInput();
		bookingInput.setMobileNum("1234567890");
		bookingInput.setOnwardFlightId(onwardFlightId);
		bookingInput.setReturnFlightId(returnFlightId);
		bookingInput.setNumOfSeats(1);
		
		result = mockMvc.perform(post("/flight/booking").content(toJson(bookingInput))).andExpect(status().isCreated())
							.andExpect(jsonPath("$.onwardBookingId").isNotEmpty())
							.andExpect(jsonPath("$.returnBookingId").isNotEmpty())
							.andExpect(jsonPath("$.numOfSeats").value(1))
							.andExpect(jsonPath("$.totalBookingAmount").value(450.0))
							.andReturn();
		String bookingResponse = result.getResponse().getContentAsString();
		Long onwardBookingId = Long.valueOf(JsonPath.read(bookingResponse, "$.onwardBookingId") + "");
		Long returnBookingId = Long.valueOf(JsonPath.read(bookingResponse, "$.returnBookingId") + "");
		
		Booking onwardBooking = bookingRepository.findOne(onwardBookingId);
		Booking returnBooking = bookingRepository.findOne(returnBookingId);
		
		assertEquals(user.getEmail(), onwardBooking.getCreatedBy());
		assertEquals(user.getEmail(), returnBooking.getCreatedBy());
		assertNotNull(onwardBooking.getCreatedDate());
		assertNotNull(returnBooking.getCreatedDate());
		
		// Add passengers
		CreatePassengersInput passengers = new CreatePassengersInput();
		BookingPassengerRecord onwardBookingPassengerRecord = new BookingPassengerRecord();
		List<PassengerInput> onwardPassengers = new ArrayList<>();
		PassengerInput passenger1 = new PassengerInput();
		passenger1.setName("Passenger One");
		passenger1.setAge(28);
		passenger1.setGender(Gender.FEMALE);
		onwardPassengers.add(passenger1);
		onwardBookingPassengerRecord.setPassengers(onwardPassengers);
		onwardBookingPassengerRecord.setBookingId(onwardBookingId);
		
		BookingPassengerRecord returnBookingPassengerRecord = new BookingPassengerRecord();
		List<PassengerInput> returnPassengers = new ArrayList<>();
		PassengerInput passenger2 = new PassengerInput();
		passenger2.setName("Passenger two");
		passenger2.setAge(25);
		passenger2.setGender(Gender.MALE);
		returnPassengers.add(passenger2);
		returnBookingPassengerRecord.setPassengers(returnPassengers);
		returnBookingPassengerRecord.setBookingId(returnBookingId);
		
		passengers.setOnwardBookingPassengerRecord(onwardBookingPassengerRecord);
		passengers.setReturnBookingPassengerRecord(returnBookingPassengerRecord);
		
		mockMvc.perform(post("/flight/booking/passengers").content(toJson(passengers))).andExpect(status().isCreated())
							.andExpect(jsonPath("$.passengers").isArray());
		
		// Make credit card payment
		CreditCardPaymentInput creditCardInput = new CreditCardPaymentInput();
		creditCardInput.setName("test");
		creditCardInput.setCreditCardNumber("1234567890123456");
		creditCardInput.setExpiryDate("");
		creditCardInput.setCvvNumber("");
		creditCardInput.setOnwardBookingId(onwardBookingId);
		creditCardInput.setReturnBookingId(returnBookingId);
	
		setRestTemplateBehaviour(PaymentStatus.SUCCESS, "");
		mockMvc.perform(post("/flight/booking/payment/credit").content(toJson(creditCardInput))).andExpect(status().isOk())
					.andExpect(jsonPath("$.paymentTxnId").isNotEmpty());
	}
	
	@Test
	@Sql(scripts = {"/setup.sql", "/user.sql", "/flight.sql", "/flight_seat.sql"})
	public void test() {
		System.out.println("test ==========> " + flightRepository.findAll().size());
	}
	
	private void setRestTemplateBehaviour(PaymentStatus status, String referenceNum) {
		
		PaymentGatewayOutput output = new PaymentGatewayOutput();
		output.setStatus(status);
		output.setReferenceNum(referenceNum);
		
		ResponseEntity<PaymentGatewayOutput> responseEntity = new ResponseEntity<>(output, HttpStatus.OK); 
		when(restTemplate.postForEntity(any(String.class), any(PaymentGatewayInput.class), Mockito.<Class<PaymentGatewayOutput>> any()))
						.thenReturn(responseEntity);
	}
}
