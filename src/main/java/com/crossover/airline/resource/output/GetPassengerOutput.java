package com.crossover.airline.resource.output;

import java.util.ArrayList;
import java.util.List;

import com.crossover.airline.resource.BaseOutput;

public class GetPassengerOutput extends BaseOutput {

	private static final long serialVersionUID = -940882853935717555L;

	private List<PassengerOutput> passengers = new ArrayList<>();
	
	private Long flightId;

	public List<PassengerOutput> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerOutput> passengers) {
		this.passengers = passengers;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}
	
	public void addPassenger(PassengerOutput passengerOutput) {
		passengers.add(passengerOutput);
	}
}
