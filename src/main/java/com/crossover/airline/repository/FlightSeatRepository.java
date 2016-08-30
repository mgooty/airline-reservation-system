package com.crossover.airline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.FlightSeat;
import com.crossover.airline.entity.Passenger;

@Repository
public interface FlightSeatRepository extends JpaRepository<FlightSeat, Long>{

	List<FlightSeat> findByFlight(Flight flight);
	
	List<FlightSeat> findByFlightAndPassenger(Flight flight, Passenger passenger);
}
