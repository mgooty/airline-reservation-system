package com.crossover.airline.service.impl;

import static com.crossover.airline.exception.AirlineError.CREATE_FLIGHT_ONWARD_BOOKING_INSUFFICIENT_SEATS;
import static com.crossover.airline.exception.AirlineError.CREATE_FLIGHT_RETURN_BOOKING_INSUFFICIENT_SEATS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crossover.airline.auth.UserContext;
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
import com.crossover.airline.resource.output.BookingDetailsOutput;
import com.crossover.airline.resource.output.BookingDetailsOutput.BookingRecord;
import com.crossover.airline.resource.output.FlightBookingOutput;
import com.crossover.airline.resource.output.FlightOutput;
import com.crossover.airline.resource.output.FlightSeatOutput;
import com.crossover.airline.resource.output.GetPassengerOutput;
import com.crossover.airline.resource.output.PassengerOutput;
import com.crossover.airline.resource.output.SearchFlightOutput;
import com.crossover.airline.service.FlightService;

@Service
public class FlightServiceImpl implements FlightService {

	Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);
	
	@Autowired
	private FlightRepository flightRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private PassengerRepository passengerRepository; 
	
	@Autowired
	private FlightSeatRepository flightSeatRepository;

	@Autowired
	private UserContext userContext;
	
	@Value("${duration.for.checkin.in.hrs:48}")
	private int durationForCheckin;
	
	@Override
	public SearchFlightOutput searchFlights(Date departureDate, String fromCity, Date returnDate, String toCity, FlightClass flightClass, int numOfSeats, boolean onlyOnwards) {
		logger.info("Search flights, departure date - {}, return date - {}, from city - {}, to city - {}, num of seats - {}, Flight class - {}, only onwards - {}",
					departureDate, returnDate, fromCity, toCity, numOfSeats, flightClass, onlyOnwards);
		
		List<Flight> flights = null;
		if(onlyOnwards) {
			flights = flightRepository.searchOnwardsFlights(departureDate, fromCity.toUpperCase(), toCity.toUpperCase(), flightClass, numOfSeats);
		} else {
			flights = flightRepository.searchFlights(departureDate, fromCity.toUpperCase(), returnDate, toCity.toUpperCase(), flightClass, numOfSeats);
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
			
			if(flight.getFromCity().equalsIgnoreCase(fromCity)) {
				output.addOnwardFlight(flightOutputResource);
			} else {
				output.addReturnFlight(flightOutputResource);
			}
		}
		
		return output;
	}

	@Override
	public FlightBookingOutput createBooking(FlightBookingInput flightBookingInput) {
		logger.info("Create booking, onward flight id - {}, return flight id - {} ", flightBookingInput.getOnwardFlightId(), flightBookingInput.getReturnFlightId());
		
		Flight onwardFlight = flightRepository.findOne(flightBookingInput.getOnwardFlightId());
		
		// Check if number of seats are available
		if(onwardFlight.getNoOfSeatsAvailable() < flightBookingInput.getNumOfSeats()) {
			throw new AirlineException(CREATE_FLIGHT_ONWARD_BOOKING_INSUFFICIENT_SEATS, flightBookingInput.getNumOfSeats(), onwardFlight.getNoOfSeatsAvailable());
		}
		
		Flight returnFlight = null;
		if(flightBookingInput.getReturnFlightId() != null) {
			returnFlight = flightRepository.findOne(flightBookingInput.getReturnFlightId());
			if(returnFlight != null && (returnFlight.getNoOfSeatsAvailable() < flightBookingInput.getNumOfSeats())) {
				throw new AirlineException(CREATE_FLIGHT_RETURN_BOOKING_INSUFFICIENT_SEATS, flightBookingInput.getNumOfSeats(), returnFlight.getNoOfSeatsAvailable());
			}
			
		}
		
		// Create booking with status PENDING_PAYMENT
		Booking onwardBooking = new Booking();
		onwardBooking.setAmount(flightBookingInput.getNumOfSeats() * onwardFlight.getPricePerSeat());
		onwardBooking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
		onwardBooking.setEmail(userContext.getCurrentUser().getEmail());
		onwardBooking.setMobileNumber(flightBookingInput.getMobileNum());
		onwardBooking.setName(userContext.getCurrentUser().getName());
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
			returnBooking.setEmail(userContext.getCurrentUser().getEmail());
			returnBooking.setMobileNumber(flightBookingInput.getMobileNum());
			returnBooking.setName(userContext.getCurrentUser().getName());
			returnBooking.setNumOfSeats(flightBookingInput.getNumOfSeats());
			returnBooking.setFlight(returnFlight);
			
			bookingRepository.save(returnBooking);
			
			returnFlight.setNoOfSeatsAvailable(returnFlight.getNoOfSeatsAvailable() - flightBookingInput.getNumOfSeats());
			flightRepository.save(returnFlight);
		}
		
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
		
		if(booking.getNumOfSeats() != bookingPassengerRecord.getPassengers().size()) {
			throw new AirlineException(AirlineError.CREATE_PASSENGER_INCORRECT_SIZE, booking.getNumOfSeats(), bookingPassengerRecord.getPassengers().size());
		}
		
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
			if(flightSeat.getPassenger() != null) {
				flightSeatOutput.setPassengerId(flightSeat.getPassenger().getId());
			}
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
		List<Long> passengerIds = new ArrayList<>();
		Map<Long, FlightSeatInput> seatsMap = new HashMap<>();
		for(FlightSeatInput flightSeatInput: checkinInput.getSeats()) {
			seatIds.add(flightSeatInput.getSeatId());
			passengerIds.add(flightSeatInput.getPassengerId());
			seatsMap.put(flightSeatInput.getSeatId(), flightSeatInput);
		}
		List<FlightSeat> flightSeats = flightSeatRepository.findAll(seatIds);
		List<Passenger> passengers = passengerRepository.findAll(passengerIds);
		
		for(FlightSeat flightSeat: flightSeats) {
			if(flightSeat.getPassenger() != null) {
				throw new AirlineException(AirlineError.FLIGHT_CHECKIN_SEAT_NOT_AVAILABLE, flightSeat.getSeatNumber());
			}
			
			FlightSeatInput flightSeatInput = seatsMap.get(flightSeat.getId());
			for(Passenger passenger: passengers) {
				if(passenger.getId() == flightSeatInput.getPassengerId()) {
					flightSeat.setPassenger(passenger);
					break;
				}
			}
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

	@Override
	public BookingDetailsOutput getMyBookings(String email) {
		List<Booking> bookings = bookingRepository.findByEmail(email);
		
		List<BookingRecord> bookingsOutput = new ArrayList<>();
		for(Booking booking: bookings) {
			BookingRecord bookingRecord = new BookingRecord();
			FlightBookingOutput bookingOutput = new FlightBookingOutput();
			bookingOutput.setNumOfSeats(booking.getNumOfSeats());
			bookingOutput.setTotalBookingAmount(booking.getAmount());
			
			FlightOutput flightOutput = new FlightOutput();
			flightOutput.setDepartureDate(booking.getFlight().getDepartureDate());
			flightOutput.setDurationInMins(booking.getFlight().getDurationInMins());
			flightOutput.setFlightClass(booking.getFlight().getFlightClass());
			flightOutput.setFlightCode(booking.getFlight().getFlightCode());
			flightOutput.setFlightStatus(booking.getFlight().getFlightStatus());
			flightOutput.setFromCity(booking.getFlight().getFromCity());
			flightOutput.setToCity(booking.getFlight().getToCity());
			
			List<PassengerOutput> passengersOutput = new ArrayList<>();
			List<Passenger> passengers = passengerRepository.findByBooking(booking);
			for(Passenger passenger: passengers) {
				PassengerOutput passengerOutput = new PassengerOutput();
				passengerOutput.setAge(passenger.getAge());
				passengerOutput.setGender(passenger.getGender());
				passengerOutput.setName(passenger.getName());
				
				passengersOutput.add(passengerOutput);

				List<FlightSeatOutput> seatsOutput = new ArrayList<>();
				List<FlightSeat> flightSeats = flightSeatRepository.findByFlightAndPassenger(booking.getFlight(), passenger);
				for(FlightSeat flightSeat: flightSeats) {
					FlightSeatOutput seatOutput = new FlightSeatOutput();
					seatOutput.setSeatNumber(flightSeat.getSeatNumber());
					
					seatsOutput.add(seatOutput);
				}
				bookingRecord.setSeats(seatsOutput);
			}
			
			bookingRecord.setBooking(bookingOutput);
			bookingRecord.setFlight(flightOutput);
			bookingRecord.setPassengers(passengersOutput);
			bookingsOutput.add(bookingRecord);
		}
		
		BookingDetailsOutput output = new BookingDetailsOutput();
		output.setBookings(bookingsOutput);
		return output;
	}

	@Override
	public void cancelBooking(Long bookingId) {
		Booking booking = bookingRepository.findOne(bookingId);
		booking.setBookingStatus(BookingStatus.CANCELLED);
		
		Flight flight = booking.getFlight();
		flight.setNoOfSeatsBooked(flight.getNoOfSeatsBooked() - booking.getNumOfSeats());
		flight.setNoOfSeatsAvailable(flight.getNoOfSeatsAvailable() + booking.getNumOfSeats());
		
		flightRepository.save(flight);
		bookingRepository.save(booking);
		
		// TODO: add a message to queue to process refund asynchrnously
	}
}
