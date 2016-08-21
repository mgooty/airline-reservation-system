package com.crossover.airline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

	List<Passenger> findByBooking(Booking booking);
}
