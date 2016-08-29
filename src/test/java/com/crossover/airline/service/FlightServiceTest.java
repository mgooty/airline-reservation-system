package com.crossover.airline.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import com.crossover.airline.BaseServiceTest;
import com.crossover.airline.auth.UserAuthentication;
import com.crossover.airline.auth.UserContext;
import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Booking.BookingStatus;
import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.FlightClass;
import com.crossover.airline.entity.FlightSeat;
import com.crossover.airline.entity.Passenger;
import com.crossover.airline.entity.Passenger.Gender;
import com.crossover.airline.entity.User.Role;
import com.crossover.airline.exception.AirlineException;
import com.crossover.airline.repository.BookingRepository;
import com.crossover.airline.repository.FlightRepository;
import com.crossover.airline.repository.FlightSeatRepository;
import com.crossover.airline.repository.PassengerRepository;
import com.crossover.airline.resource.input.CheckinInput;
import com.crossover.airline.resource.input.CheckinInput.FlightSeatInput;
import com.crossover.airline.resource.input.CreatePassengersInput;
import com.crossover.airline.resource.input.CreatePassengersInput.BookingPassengerRecord;
import com.crossover.airline.resource.input.FlightBookingInput;
import com.crossover.airline.resource.input.PassengerInput;
import com.crossover.airline.resource.output.FlightBookingOutput;
import com.crossover.airline.resource.output.FlightSeatOutput;
import com.crossover.airline.resource.output.GetPassengerOutput;
import com.crossover.airline.resource.output.SearchFlightOutput;
import com.crossover.airline.service.impl.FlightServiceImpl;

public class FlightServiceTest extends BaseServiceTest {

	@InjectMocks
	FlightService unit = new FlightServiceImpl();
	
	@Mock
	private FlightRepository flightRepository;
	
	@Mock
	private BookingRepository bookingRepository;
	
	@Mock
	private PassengerRepository passengerRepository; 
	
	@Mock
	private FlightSeatRepository flightSeatRepository;

	@Mock
	private UserContext userContext;
	
	@Test
	public void testIfOnwardAndReturnFlightsAreReturnedSuccessfully() {
		// Given
		Date departureDate = new Date();
		Date returnDate = new Date();
		String fromCity = "BANGALORE";
		String toCity = "DELHI";
		int numOfSeats = 2;
		boolean onlyOnwards = false;
		FlightClass flightClass = FlightClass.ECONOMY;
		
		List<Flight> flights = new ArrayList<>();
		Flight onwardFlight = new Flight();
		onwardFlight.setFlightCode("F001");
		onwardFlight.setFromCity(fromCity);
		onwardFlight.setToCity(toCity);
		
		Flight returnFlight = new Flight();
		returnFlight.setFlightCode("F002");
		returnFlight.setFromCity(toCity);
		returnFlight.setToCity(fromCity);
		
		flights.add(onwardFlight);
		flights.add(returnFlight);
		
		when(flightRepository.searchFlights(departureDate, fromCity.toUpperCase(), returnDate, toCity.toUpperCase(), flightClass, numOfSeats)).thenReturn(flights);
		
		// When
		SearchFlightOutput output = unit.searchFlights(departureDate, fromCity, returnDate, toCity, flightClass, numOfSeats, onlyOnwards);
		
		// Then
		assertEquals(1, output.getOnwardFlights().size());
		assertEquals(1, output.getReturnFlights().size());
		
		assertEquals(onwardFlight.getFlightCode(), output.getOnwardFlights().get(0).getFlightCode());
		assertEquals(onwardFlight.getFromCity(), output.getOnwardFlights().get(0).getFromCity());
		assertEquals(onwardFlight.getToCity(), output.getOnwardFlights().get(0).getToCity());
		
		assertEquals(returnFlight.getFlightCode(), output.getReturnFlights().get(0).getFlightCode());
		assertEquals(returnFlight.getFromCity(), output.getReturnFlights().get(0).getFromCity());
		assertEquals(returnFlight.getToCity(), output.getReturnFlights().get(0).getToCity());
		
		verify(flightRepository).searchFlights(departureDate, fromCity.toUpperCase(), returnDate, toCity.toUpperCase(), flightClass, numOfSeats);
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfOnlyOnwardFlightsAreReturned() {
		// Given
		Date departureDate = new Date();
		Date returnDate = new Date();
		String fromCity = "BANGALORE";
		String toCity = "DELHI";
		int numOfSeats = 2;
		boolean onlyOnwards = true;
		FlightClass flightClass = FlightClass.ECONOMY;
		
		List<Flight> flights = new ArrayList<>();
		Flight onwardFlight = new Flight();
		onwardFlight.setFlightCode("F001");
		onwardFlight.setFromCity(fromCity);
		onwardFlight.setToCity(toCity);
		
		flights.add(onwardFlight);
		
		when(flightRepository.searchOnwardsFlights(departureDate, fromCity, toCity, flightClass, numOfSeats)).thenReturn(flights);
		
		// When
		SearchFlightOutput output = unit.searchFlights(departureDate, fromCity, returnDate, toCity, flightClass, numOfSeats, onlyOnwards);
		
		// Then
		assertEquals(1, output.getOnwardFlights().size());
		assertEquals(0, output.getReturnFlights().size());
		
		assertEquals(onwardFlight.getFlightCode(), output.getOnwardFlights().get(0).getFlightCode());
		assertEquals(onwardFlight.getFromCity(), output.getOnwardFlights().get(0).getFromCity());
		assertEquals(onwardFlight.getToCity(), output.getOnwardFlights().get(0).getToCity());
		
		verify(flightRepository).searchOnwardsFlights(departureDate, fromCity, toCity, flightClass, numOfSeats);
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfBookingIsCreatedForOnwardAndReturnJourneySuccessfully() {
		// Given
		Long onwardFlightId = 12345l;
		Long returnFlightId = 67890l;
		String userEmail = "test@abc.com";
		String userName = "test";
		
		Flight onwardFlight = new Flight();
		onwardFlight.setId(onwardFlightId);
		onwardFlight.setNoOfSeatsAvailable(5);
		onwardFlight.setFlightCode("F001");
		onwardFlight.setPricePerSeat(100.0);
		
		Flight returnFlight = new Flight();
		returnFlight.setId(returnFlightId);
		returnFlight.setNoOfSeatsAvailable(5);
		returnFlight.setFlightCode("F002");
		returnFlight.setPricePerSeat(200.0);
		
		when(flightRepository.findOne(onwardFlightId)).thenReturn(onwardFlight);
		when(flightRepository.findOne(returnFlightId)).thenReturn(returnFlight);
		
		UserAuthentication loggedInUser = new UserAuthentication(userEmail, userName, Role.ROLE_PUBLIC);
		when(userContext.getCurrentUser()).thenReturn(loggedInUser);
		
		FlightBookingInput flightBookingInput = new FlightBookingInput();
		flightBookingInput.setOnwardFlightId(onwardFlightId);
		flightBookingInput.setReturnFlightId(returnFlightId);
		flightBookingInput.setNumOfSeats(2);
		flightBookingInput.setMobileNum("1234567890");
		
		Booking onwardBooking = new Booking();
		onwardBooking.setAmount(flightBookingInput.getNumOfSeats() * onwardFlight.getPricePerSeat());
		onwardBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		onwardBooking.setEmail(loggedInUser.getEmail());
		onwardBooking.setMobileNumber(flightBookingInput.getMobileNum());
		onwardBooking.setName(loggedInUser.getName());
		onwardBooking.setNumOfSeats(flightBookingInput.getNumOfSeats());
		onwardBooking.setFlight(onwardFlight);
		
		Booking returnBooking = new Booking();
		returnBooking.setAmount(flightBookingInput.getNumOfSeats() * returnFlight.getPricePerSeat());
		returnBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		returnBooking.setEmail(loggedInUser.getEmail());
		returnBooking.setMobileNumber(flightBookingInput.getMobileNum());
		returnBooking.setName(loggedInUser.getName());
		returnBooking.setNumOfSeats(flightBookingInput.getNumOfSeats());
		returnBooking.setFlight(returnFlight);
		
		// When
		FlightBookingOutput output = unit.createBooking(flightBookingInput);
		
		// Then
		assertEquals(600.0, output.getTotalBookingAmount(), 0);
		assertEquals(3, onwardFlight.getNoOfSeatsAvailable(), 0);
		assertEquals(3, returnFlight.getNoOfSeatsAvailable(), 0);
		
		verify(flightRepository).findOne(onwardFlightId);
		verify(flightRepository).findOne(returnFlightId);
		verify(userContext, times(4)).getCurrentUser();
		verify(flightRepository).save(onwardFlight);
		verify(flightRepository).save(returnFlight);
		verify(bookingRepository).save(onwardBooking);
		verify(bookingRepository).save(returnBooking);
		
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfBookingIsCreatedForOnwardJourneyOnlySuccessfully() {
		// Given
		Long onwardFlightId = 12345l;
		String userEmail = "test@abc.com";
		String userName = "test";
		
		Flight onwardFlight = new Flight();
		onwardFlight.setId(onwardFlightId);
		onwardFlight.setNoOfSeatsAvailable(5);
		onwardFlight.setFlightCode("F001");
		onwardFlight.setPricePerSeat(100.0);
		
		when(flightRepository.findOne(onwardFlightId)).thenReturn(onwardFlight);
		
		UserAuthentication loggedInUser = new UserAuthentication(userEmail, userName, Role.ROLE_PUBLIC);
		when(userContext.getCurrentUser()).thenReturn(loggedInUser);
		
		FlightBookingInput flightBookingInput = new FlightBookingInput();
		flightBookingInput.setOnwardFlightId(onwardFlightId);
		flightBookingInput.setNumOfSeats(2);
		flightBookingInput.setMobileNum("1234567890");
		
		Booking onwardBooking = new Booking();
		onwardBooking.setAmount(flightBookingInput.getNumOfSeats() * onwardFlight.getPricePerSeat());
		onwardBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		onwardBooking.setEmail(loggedInUser.getEmail());
		onwardBooking.setMobileNumber(flightBookingInput.getMobileNum());
		onwardBooking.setName(loggedInUser.getName());
		onwardBooking.setNumOfSeats(flightBookingInput.getNumOfSeats());
		onwardBooking.setFlight(onwardFlight);
		
		// When
		FlightBookingOutput output = unit.createBooking(flightBookingInput);
		
		// Then
		assertEquals(200.0, output.getTotalBookingAmount(), 0);
		assertEquals(3, onwardFlight.getNoOfSeatsAvailable(), 0);
		
		verify(flightRepository).findOne(onwardFlightId);
		verify(userContext, times(2)).getCurrentUser();
		verify(flightRepository).save(onwardFlight);
		verify(bookingRepository).save(onwardBooking);
		
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfPassengersAreAddedToBookingSuccessfully() {
		// Given
		Long onwardBookingId = 12345l;
		
		CreatePassengersInput intput = new CreatePassengersInput();
		BookingPassengerRecord onwardPassengersRecord = new BookingPassengerRecord();
		List<PassengerInput> onwardPassengers = new ArrayList<>();
		PassengerInput onwardPassenger = new PassengerInput();
		onwardPassenger.setAge(25);
		onwardPassenger.setGender(Gender.MALE);
		onwardPassenger.setName("Test passenger");
		onwardPassengers.add(onwardPassenger);
		onwardPassengersRecord.setPassengers(onwardPassengers);
		onwardPassengersRecord.setBookingId(onwardBookingId);
		intput.setOnwardBookingPassengerRecord(onwardPassengersRecord); 
		
		Booking onwardBooking = new Booking();
		onwardBooking.setId(onwardBookingId);
		onwardBooking.setNumOfSeats(1);
		
		List<Passenger> passengers = new ArrayList<>();
		
		Passenger passenger = new Passenger();
		passenger.setName("test passenger");
		passenger.setAge(25);
		passenger.setGender(Gender.MALE);
		passenger.setBooking(onwardBooking);
		passengers.add(passenger);
		
		when(bookingRepository.findOne(onwardBookingId)).thenReturn(onwardBooking);
		
		// When
		GetPassengerOutput output = unit.createPassengers(intput);
		
		// Then
		assertEquals(1, output.getPassengers().size());
		
		verify(bookingRepository).findOne(onwardBookingId);
		verify(passengerRepository).save(Matchers.anyListOf(Passenger.class));
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfPassengersAreReturnedForABooking() {
		// Given
		Long bookingId = 123456l;
		
		Flight flight = new Flight();
		flight.setId(98765l);
		
		Booking booking = new Booking();
		booking.setId(bookingId);
		booking.setNumOfSeats(1);
		booking.setFlight(flight);
		when(bookingRepository.findOne(bookingId)).thenReturn(booking);
		
		List<Passenger> passengers = new ArrayList<>();
		
		Passenger passenger = new Passenger();
		passenger.setName("test passenger");
		passenger.setAge(25);
		passenger.setGender(Gender.MALE);
		passenger.setBooking(booking);
		passengers.add(passenger);
		
		when(passengerRepository.findByBooking(booking)).thenReturn(passengers);
		// When
		GetPassengerOutput output = unit.getPassengers(bookingId);
		
		// Then
		assertEquals(1, output.getPassengers().size());
		
		verify(bookingRepository).findOne(bookingId);
		verify(passengerRepository).findByBooking(booking);
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfSeatsInFlightAreReturnedSuccessfully() {
		// Given
		Long flightId = 123456l;
		
		Flight flight = new Flight();
		flight.setId(flightId);
		
		List<FlightSeat> flightSeats = new ArrayList<>();
		FlightSeat seat1 = new FlightSeat();
		seat1.setId(1234l);
		seat1.setSeatNumber(1);
		seat1.setFlight(flight);
		
		FlightSeat seat2 = new FlightSeat();
		seat2.setId(12345l);
		seat2.setSeatNumber(2);
		seat2.setFlight(flight);
		
		flightSeats.add(seat1);
		flightSeats.add(seat2);
		when(flightSeatRepository.findByFlight(flight)).thenReturn(flightSeats);
		
		// When
		List<FlightSeatOutput> output = unit.getFlightSeats(flightId);
		
		// Then
		assertEquals(2, output.size());
		
		verify(flightSeatRepository).findByFlight(flight);
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfCheckinIsSuccessfull() {
		// Given
		Long bookingId = 12345l;
		Long seatId = 98765l;
		Long passengerId = 83493l;
		int seatNumber = 12;
		Long flightId = 937493l;
		
		Flight flight = new Flight();
		flight.setId(flightId);
		long currentTimeinMillis = System.currentTimeMillis();
		flight.setDepartureDate(new Date(currentTimeinMillis - (60 * 1000)));
		
		Booking booking = new Booking();
		booking.setId(bookingId);
		booking.setFlight(flight);
		when(bookingRepository.findOne(bookingId)).thenReturn(booking);
		when(flightRepository.findOne(booking.getFlight().getId())).thenReturn(flight);
		
		List<Long> seatIds = new ArrayList<>();
		seatIds.add(seatId);
		
		List<FlightSeat> seats = new ArrayList<>();
		FlightSeat seat = new FlightSeat();
		seat.setId(seatId);
		seat.setSeatNumber(seatNumber);
		seats.add(seat);
		when(flightSeatRepository.findAll(seatIds)).thenReturn(seats);
		
		List<Long> passengerIds = new ArrayList<>();
		passengerIds.add(passengerId);
		
		List<Passenger> passengers = new ArrayList<>();
		Passenger passenger = new Passenger();
		passenger.setId(passengerId);
		passenger.setName("test passenger");
		passenger.setAge(25);
		passenger.setGender(Gender.MALE);
		passengers.add(passenger);
		
		when(passengerRepository.findAll(passengerIds)).thenReturn(passengers);
		
		// When
		CheckinInput input = new CheckinInput();
		List<FlightSeatInput> seatsInput = new ArrayList<>();
		FlightSeatInput seatInput = new FlightSeatInput();
		seatInput.setSeatId(seatId);
		seatInput.setPassengerId(passengerId);
		seatInput.setSeatNumber(seatNumber);
		seatsInput.add(seatInput);
		input.setSeats(seatsInput);
		
		unit.checkIn(bookingId, input);
		
		// Then
		verify(bookingRepository).findOne(bookingId);
		verify(flightRepository).findOne(booking.getFlight().getId());
		verify(flightSeatRepository).findAll(seatIds);
		verify(passengerRepository).findAll(passengerIds);
		
		seats.get(0).setPassenger(passenger);
		verify(flightSeatRepository).save(seats);
		
		verifyNoMoreInteractions(flightRepository, bookingRepository, passengerRepository, flightSeatRepository, userContext);
	}
	
	@Test
	public void testIfErrorThrownIfCheckinBeforeConfiguredTime() {
		// Given
		Long bookingId = 12345l;
		Long seatId = 98765l;
		Long passengerId = 83493l;
		int seatNumber = 12;
		Long flightId = 937493l;
		
		Flight flight = new Flight();
		flight.setId(flightId);
		flight.setDepartureDate(new Date());
		
		Booking booking = new Booking();
		booking.setId(bookingId);
		booking.setFlight(flight);
		when(bookingRepository.findOne(bookingId)).thenReturn(booking);
		when(flightRepository.findOne(booking.getFlight().getId())).thenReturn(flight);
		
		// When
		CheckinInput input = new CheckinInput();
		List<FlightSeatInput> seatsInput = new ArrayList<>();
		FlightSeatInput seatInput = new FlightSeatInput();
		seatInput.setSeatId(seatId);
		seatInput.setPassengerId(passengerId);
		seatInput.setSeatNumber(seatNumber);
		seatsInput.add(seatInput);
		input.setSeats(seatsInput);
		
		try {
			unit.checkIn(bookingId, input);
		} catch(AirlineException e) {
			// Then
			assertEquals("FCE002", e.getErrorCode());
			assertEquals("You can check-in only 0 Hrs within the departure time", e.getMessage());
		}
	}
	
	@Test
	public void testIfErrorThrownWhenNumOfPassengerAndNumOfSeatsIsInvalid() {
		// Given
		Long onwardBookingId = 12345l;
		
		CreatePassengersInput input = new CreatePassengersInput();
		BookingPassengerRecord onwardPassengersRecord = new BookingPassengerRecord();
		List<PassengerInput> onwardPassengers = new ArrayList<>();
		PassengerInput onwardPassenger = new PassengerInput();
		onwardPassenger.setAge(25);
		onwardPassenger.setGender(Gender.MALE);
		onwardPassenger.setName("Test passenger");
		onwardPassengers.add(onwardPassenger);
		onwardPassengersRecord.setPassengers(onwardPassengers);
		onwardPassengersRecord.setBookingId(onwardBookingId);
		input.setOnwardBookingPassengerRecord(onwardPassengersRecord); 
		
		Booking onwardBooking = new Booking();
		onwardBooking.setId(onwardBookingId);
		onwardBooking.setNumOfSeats(2);
		
		List<Passenger> passengers = new ArrayList<>();
		
		Passenger passenger = new Passenger();
		passenger.setName("test passenger");
		passenger.setAge(25);
		passenger.setGender(Gender.MALE);
		passenger.setBooking(onwardBooking);
		passengers.add(passenger);
		
		when(bookingRepository.findOne(onwardBookingId)).thenReturn(onwardBooking);
		
		// When
		try {
			unit.createPassengers(input);
		} catch(AirlineException e) {
			// Then
			assertEquals("CP001", e.getErrorCode());
			assertEquals("Invalid number of passengers. Number of seats booked is 2, but number of passenger added is 1", e.getMessage());
		}
	}
	
	@Test
	public void testIfErrorThrownWhenAvailableSeatsIsLessThanReturnBookingSeats() {
		// Given
		Long onwardFlightId = 12345l;
		Long returnFlightId = 67890l;
		String userEmail = "test@abc.com";
		String userName = "test";
		
		Flight onwardFlight = new Flight();
		onwardFlight.setId(onwardFlightId);
		onwardFlight.setNoOfSeatsAvailable(5);
		
		Flight returnFlight = new Flight();
		returnFlight.setId(returnFlightId);
		returnFlight.setNoOfSeatsAvailable(0);
		
		when(flightRepository.findOne(onwardFlightId)).thenReturn(onwardFlight);
		when(flightRepository.findOne(returnFlightId)).thenReturn(returnFlight);
		
		UserAuthentication loggedInUser = new UserAuthentication(userEmail, userName, Role.ROLE_PUBLIC);
		when(userContext.getCurrentUser()).thenReturn(loggedInUser);
		
		FlightBookingInput flightBookingInput = new FlightBookingInput();
		flightBookingInput.setOnwardFlightId(onwardFlightId);
		flightBookingInput.setReturnFlightId(returnFlightId);
		flightBookingInput.setNumOfSeats(2);
		flightBookingInput.setMobileNum("1234567890");
		
		// When
		try {
			unit.createBooking(flightBookingInput);
		} catch(AirlineException e) {
			// Then
			assertEquals("CFB007", e.getErrorCode());
			assertEquals("We do not have 2 seats available for your return journey. Only 0 seats are available for booking", e.getMessage());
		}
	}
	
	@Test
	public void testIfErrorThrownWhenAvailableSeatsIsLessThanOnwardBookingSeats() {
		// Given
		Long onwardFlightId = 12345l;
		Long returnFlightId = 67890l;
		String userEmail = "test@abc.com";
		String userName = "test";
		
		Flight onwardFlight = new Flight();
		onwardFlight.setId(onwardFlightId);
		onwardFlight.setNoOfSeatsAvailable(1);
		
		Flight returnFlight = new Flight();
		returnFlight.setId(returnFlightId);
		returnFlight.setNoOfSeatsAvailable(5);
		
		when(flightRepository.findOne(onwardFlightId)).thenReturn(onwardFlight);
		when(flightRepository.findOne(returnFlightId)).thenReturn(returnFlight);
		
		UserAuthentication loggedInUser = new UserAuthentication(userEmail, userName, Role.ROLE_PUBLIC);
		when(userContext.getCurrentUser()).thenReturn(loggedInUser);
		
		FlightBookingInput flightBookingInput = new FlightBookingInput();
		flightBookingInput.setOnwardFlightId(onwardFlightId);
		flightBookingInput.setReturnFlightId(returnFlightId);
		flightBookingInput.setNumOfSeats(2);
		flightBookingInput.setMobileNum("1234567890");
		
		// When
		try {
			unit.createBooking(flightBookingInput);
		} catch(AirlineException e) {
			// Then
			assertEquals("CFB008", e.getErrorCode());
			assertEquals("We do not have 2 seats available for your onward journey. Only 1 seats are available for booking", e.getMessage());
		}
	}
}
