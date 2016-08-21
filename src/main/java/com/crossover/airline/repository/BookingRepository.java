package com.crossover.airline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crossover.airline.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{

}
