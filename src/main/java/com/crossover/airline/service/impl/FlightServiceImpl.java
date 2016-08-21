package com.crossover.airline.service.impl;

import static com.crossover.airline.exception.AirlineError.CREATE_FLIGHT_ONWARD_BOOKING_INSUFFICIENT_SEATS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Booking.BookingStatus;
import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.FlightClass;
import com.crossover.airline.entity.FlightSeat;
import com.crossover.airline.entity.Passenger;
import com.crossover.airline.exception.AirlineError;
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
import com.crossover.airline.resource.output.FlightOutput;
import com.crossover.airline.resource.output.FlightSeatOutput;
import com.crossover.airline.resource.output.GetPassengerOutput;
import com.crossover.airline.resource.output.PassengerOutput;
import com.crossover.airline.resource.output.SearchFlightOutput;
import com.crossover.airline.service.FlightService;

@Service
public class FlightServiceImpl implements FlightService {

	@Autowired
	private FlightRepository flightRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private PassengerRepository passengerRepository; 
	
	@Autowired
	private FlightSeatRepository flightSeatRepository;

	@Value("${duration.for.checkin.in.hrs:48}")
	private int durationForCheckin;
	
	@Override
	public SearchFlightOutput searchFlights(Date departureDate, String fromCity, Date returnDate, String toCity, FlightClass flightClass, int numOfSeats, boolean onlyOnwards) {
		
		List<Flight> flights = null;
		if(onlyOnwards) {
			flights = flightRepository.searchOnwardsFlights(departureDate, fromCity, toCity, flightClass, numOfSeats);
		} else {
			flights = flightRepository.searchFlights(departureDate, fromCity, returnDate, toCity, flightClass, numOfSeats);
		}
		
		SearchFlightOutput output = new SearchFlightOutput();
		for(Flight flight: flights) {
			FlightOutput flightOutputResource = new FlightOutput();
			flightOutputResource.setId(flight.getId());
			flightOutputResource.setFlightCode(flight.getFlightCode());
			flightOutputResource.setDepartureDate(flight.getDepartureDate());
			flightOutputResource.setFromCity(flight.getFromCity());
			flightOutputResource.setToCity(flight.getToCity());
			flightOutputResource.setDurationInMins(flight.getDurationInMins());
			flightOutputResource.setFlightStatus(flight.getFlightStatus());
			flightOutputResource.setFlightClass(flight.getFlightClass());
			flightOutputResource.setPricePerSeat(flight.getPricePerSeat());
			
			if(flight.getFromCity().equals(fromCity)) {
				output.addOnwardFlight(flightOutputResource);
			} else {
				output.addReturnFlight(flightOutputResource);
			}
		}
		
		return output;
	}

	@Override
	public FlightBookingOutput createBooking(FlightBookingInput flightBookingInput) {
		Flight onwardFlight = flightRepository.findOne(flightBookingInput.getOnwardFlightId());
		Flight returnFlight = flightRepository.findOne(flightBookingInput.getReturnFlightId());
		
		// Check if number of seats are available
		if(onwardFlight.getNoOfSeatsAvailable() < flightBookingInput.getNumOfSeats()) {
			throw new AirlineException(CREATE_FLIGHT_ONWARD_BOOKING_INSUFFICIENT_SEATS, flightBookingInput.getNumOfSeats(), onwardFlight.getNoOfSeatsAvailable());
		}
		
		// Create booking with status PENDING_PAYMENT
		Booking onwardBooking = new Booking();
		onwardBooking.setAmount(flightBookingInput.getNumOfSeats() * onwardFlight.getPricePerSeat());
		onwardBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		onwardBooking.setEmail(flightBookingInput.getEmail());
		onwardBooking.setMobileNumber(flightBookingInput.getMobileNum());
		onwardBooking.setName(flightBookingInput.getName());
		onwardBooking.setNumOfSeats(flightBookingInput.getNumOfSeats());
		onwardBooking.setFlight(onwardFlight);
		bookingRepository.save(onwardBooking);
		
		// Reduce number of available seats
		onwardFlight.setNoOfSeatsAvailable(onwardFlight.getNoOfSeatsAvailable() - flightBookingInput.getNumOfSeats());
		flightRepository.save(onwardFlight);

		Booking returnBooking = new Booking();
		if(flightBookingInput.getReturnFlightId() != null && flightBookingInput.getReturnFlightId() != 0) {
			returnBooking.setAmount(flightBookingInput.getNumOfSeats() * returnFlight.getPricePerSeat());
			returnBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
			returnBooking.setEmail(flightBookingInput.getEmail());
			returnBooking.setMobileNumber(flightBookingInput.getMobileNum());
			returnBooking.setName(flightBookingInput.getName());
			returnBooking.setNumOfSeats(flightBookingInput.getNumOfSeats());
			returnBooking.setFlight(returnFlight);
			
			bookingRepository.save(returnBooking);
		}
		
		returnFlight.setNoOfSeatsAvailable(returnFlight.getNoOfSeatsAvailable() - flightBookingInput.getNumOfSeats());
		flightRepository.save(returnFlight);
		
		FlightBookingOutput flightBookingOutput = new FlightBookingOutput();
		flightBookingOutput.setOnwardBookingId(onwardBooking.getId());
		flightBookingOutput.setReturnBookingId(returnBooking.getId());
		flightBookingOutput.setNumOfSeats(flightBookingInput.getNumOfSeats());
		flightBookingOutput.setTotalBookingAmount(onwardBooking.getAmount() + returnBooking.getAmount());
		
		return flightBookingOutput;
	}

	@Override
	public GetPassengerOutput createPassengers(CreatePassengersInput createPassengersInput) {
		GetPassengerOutput output = new GetPassengerOutput();
		addPassengerToBooking(createPassengersInput.getOnwardBookingPassengerRecord(), output);
		
		if(createPassengersInput.getReturnBookingPassengerRecord() != null) {
			addPassengerToBooking(createPassengersInput.getReturnBookingPassengerRecord(), output);
		}
		
		return output;
	}
	
	private void addPassengerToBooking(BookingPassengerRecord bookingPassengerRecord, GetPassengerOutput output) {
		Booking booking = bookingRepository.findOne(bookingPassengerRecord.getBookingId());
		
		List<Passenger> passengers = new ArrayList<>();
		Passenger passenger = null;
		for(PassengerInput passengerInput: bookingPassengerRecord.getPassengers()) {
			passenger = new Passenger();
			passenger.setName(passengerInput.getName());
			passenger.setAge(passengerInput.getAge());
			passenger.setGender(passengerInput.getGender());
			passenger.setBooking(booking);
			
			passengers.add(passenger);
		}
		passengerRepository.save(passengers);
		
		PassengerOutput passengerOutput = null;
		for(Passenger passengerFromDb: passengers) {
			passengerOutput = new PassengerOutput();
			passengerOutput.setPassengerId(passengerFromDb.getId());
			passengerOutput.setName(passengerFromDb.getName());
			passengerOutput.setAge(passengerFromDb.getAge());
			passengerOutput.setGender(passengerFromDb.getGender());
			
			output.addPassenger(passengerOutput);
		}
	}
	
	@Override
	public GetPassengerOutput getPassengers(Long bookingId) {
		Booking booking = bookingRepository.findOne(bookingId);
		
		List<Passenger> passengers = passengerRepository.findByBooking(booking);
		List<PassengerOutput> output = new ArrayList<>();
		PassengerOutput passengerOutput = null;
		for(Passenger passenger: passengers) {
			passengerOutput = new PassengerOutput();
			passengerOutput.setPassengerId(passenger.getId());
			passengerOutput.setName(passenger.getName());
			passengerOutput.setGender(passenger.getGender());
			passengerOutput.setAge(passenger.getAge());
			output.add(passengerOutput);
		}
		
		GetPassengerOutput getPassengerOutput = new GetPassengerOutput();
		getPassengerOutput.setPassengers(output);
		getPassengerOutput.setFlightId(booking.getFlight().getId());
		
		return getPassengerOutput;
	}

	@Override
	public List<FlightSeatOutput> getFlightSeats(Long flightId) {
		Flight flight = new Flight();
		flight.setId(flightId);
		List<FlightSeat> flightSeats = flightSeatRepository.findByFlight(flight);
		List<FlightSeatOutput> output = new ArrayList<>();
		FlightSeatOutput flightSeatOutput = null;
		
		for(FlightSeat flightSeat: flightSeats) {
			flightSeatOutput = new FlightSeatOutput();
			flightSeatOutput.setFlightId(flightSeat.getFlight().getId());
			flightSeatOutput.setPassengerId(flightSeat.getPassenger().getId());
			flightSeatOutput.setSeatNumber(flightSeat.getSeatNumber());
			flightSeatOutput.setSeatId(flightSeat.getId());
			
			output.add(flightSeatOutput);
		}
		
		return output;
	}

	@Override
	public void checkIn(Long bookingId, CheckinInput checkinInput) {
		if(!isCheckinAllowed(bookingId)) {
			throw new AirlineException(AirlineError.FLIGHT_CHECKIN_NOT_ALLOWED, durationForCheckin);
		}
		
		List<Long> seatIds = new ArrayList<>();
		Map<Long, FlightSeatInput> seatsMap = new HashMap<>();
		for(FlightSeatInput flightSeatInput: checkinInput.getSeats()) {
			seatIds.add(flightSeatInput.getSeatId());
			seatsMap.put(flightSeatInput.getSeatId(), flightSeatInput);
		}
		List<FlightSeat> flightSeats = flightSeatRepository.findAll(seatIds);
		
		for(FlightSeat flightSeat: flightSeats) {
			if(flightSeat.getSeatNumber() != 0) {
				throw new AirlineException(AirlineError.FLIGHT_CHECKIN_SEAT_NOT_AVAILABLE, flightSeat.getSeatNumber());
			}
			
			flightSeat.setSeatNumber(seatsMap.get(flightSeat.getId()).getSeatNumber());
		}
		
		flightSeatRepository.save(flightSeats);
	}
	
	private boolean isCheckinAllowed(Long bookingId) {
		Booking booking = bookingRepository.findOne(bookingId);
		Flight flight = flightRepository.findOne(booking.getFlight().getId());
		
		long durationForCheckinInMillis = durationForCheckin * 60 * 60 * 1000;
		
		long durationBeforeDeparture = flight.getDepartureDate().getTime() - System.currentTimeMillis();
		
		if(durationBeforeDeparture <= durationForCheckinInMillis) {
			return true;
		}
		
		return false;
	}
}
