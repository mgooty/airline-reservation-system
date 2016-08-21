package com.crossover.airline.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Booking extends BaseEntity {

//	public enum FlightClass {
//		ECONOMY, BUSINESS
//	}
	
	private static final long serialVersionUID = -5115869753747332473L;

	public enum BookingStatus {
		WAITING, PENDING_PAYMENT, CONFIRMED, PAYMENT_FAILED, CANCELLED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "flight_id")
	private Flight flight;
	
	@Column(name = "num_of_seats", nullable = false)
	private int numOfSeats;
	
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus = BookingStatus.PENDING_PAYMENT;
	
	@Column(name = "amount", nullable = false)
	private double amount;
	
	@Column(name = "mobile_num", nullable = false, length = 10)
	private String mobileNumber;
	
	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "name", nullable = false)
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public int getNumOfSeats() {
		return numOfSeats;
	}

	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
