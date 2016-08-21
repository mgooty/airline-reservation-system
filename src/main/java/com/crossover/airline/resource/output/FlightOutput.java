package com.crossover.airline.resource.output;

import java.util.Date;

import com.crossover.airline.entity.Flight.FlightStatus;
import com.crossover.airline.resource.BaseOutput;
import com.crossover.airline.entity.FlightClass;

public class FlightOutput extends BaseOutput {

	private static final long serialVersionUID = 4669690663584288270L;

	private Long id;

	private String flightCode;
	
	private Date departureDate;
	
	private String fromCity;
	
	private String toCity;
	
	private int durationInMins;
	
	private FlightStatus flightStatus;
	
	private FlightClass flightClass;
	
	private Double pricePerSeat;
	
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

	public Double getPricePerSeat() {
		return pricePerSeat;
	}

	public void setPricePerSeat(Double pricePerSeat) {
		this.pricePerSeat = pricePerSeat;
	}
}
