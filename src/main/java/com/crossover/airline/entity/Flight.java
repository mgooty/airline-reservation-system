package com.crossover.airline.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Flight extends BaseEntity {

	private static final long serialVersionUID = -1775543068619893106L;

	public enum FlightStatus {
		SCHEDULED, DELAYED, CANCELLED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "flight_code", nullable = false)
	private String flightCode;
	
	@Column(name = "departure_date", nullable = false)
	private Date departureDate;
	
	@Column(name = "from_city", nullable = false)
	private String fromCity;
	
	@Column(name = "to_city", nullable = false)
	private String toCity;
	
	@Column(name = "duration_in_mins", nullable = false)
	private int durationInMins;
	
	@Column(name = "flight_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private FlightStatus flightStatus = FlightStatus.SCHEDULED;
	
	@Column(name = "class", nullable = false)
	@Enumerated(EnumType.STRING)
	private FlightClass flightClass;
	
	@Column(name = "total_no_of_seats", nullable = false)
	private int totalNoOfSeats;
	
	@Column(name = "no_of_seats_available", nullable = false)
	private int noOfSeatsAvailable;
	
	@Column(name = "no_of_seats_booked", nullable = true)
	private int noOfSeatsBooked;
	
	@Column(name = "price_per_seat", nullable = false)
	private Double pricePerSeat;
	
	@Column(name  = "lockId", nullable = false)
	@Version
	private long lockId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFlightCode() {
		return flightCode;
	}

	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public int getDurationInMins() {
		return durationInMins;
	}

	public void setDurationInMins(int durationInMins) {
		this.durationInMins = durationInMins;
	}

	public FlightStatus getFlightStatus() {
		return flightStatus;
	}

	public void setFlightStatus(FlightStatus flightStatus) {
		this.flightStatus = flightStatus;
	}

	public FlightClass getFlightClass() {
		return flightClass;
	}

	public void setFlightClass(FlightClass flightClass) {
		this.flightClass = flightClass;
	}

	public int getTotalNoOfSeats() {
		return totalNoOfSeats;
	}

	public void setTotalNoOfSeats(int totalNoOfSeats) {
		this.totalNoOfSeats = totalNoOfSeats;
	}

	public int getNoOfSeatsAvailable() {
		return noOfSeatsAvailable;
	}

	public void setNoOfSeatsAvailable(int noOfSeatsAvailable) {
		this.noOfSeatsAvailable = noOfSeatsAvailable;
	}

	public int getNoOfSeatsBooked() {
		return noOfSeatsBooked;
	}

	public void setNoOfSeatsBooked(int noOfSeatsBooked) {
		this.noOfSeatsBooked = noOfSeatsBooked;
	}

	public Double getPricePerSeat() {
		return pricePerSeat;
	}

	public void setPricePerSeat(Double pricePerSeat) {
		this.pricePerSeat = pricePerSeat;
	}

	public long getLockId() {
		return lockId;
	}

	public void setLockId(long lockId) {
		this.lockId = lockId;
	}
}
