package com.crossover.airline.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crossover.airline.entity.Flight;
import com.crossover.airline.entity.FlightClass;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long>{

	@Query("select f from Flight f where (DATE(f.departureDate) = ?1 and f.fromCity = ?2 and f.toCity = ?4  and f.flightClass = ?5 and f.noOfSeatsAvailable >= ?6) or (DATE(f.departureDate) = ?3 and f.fromCity = ?4 and f.toCity = ?2  and f.flightClass = ?5 and f.noOfSeatsAvailable >= ?6)")
	public List<Flight> searchFlights(Date departureDate, String fromCity, Date returnDate, String toCity, FlightClass fightClass, int numOfSeats);

	@Query("select f from Flight f where DATE(f.departureDate) = ?1 and f.fromCity = ?2 and f.toCity = ?3  and f.flightClass = ?4 and f.noOfSeatsAvailable >= ?5")
	public List<Flight> searchOnwardsFlights(Date departureDate, String fromCity, String toCity, FlightClass flightClass, int numOfSeats);
}
