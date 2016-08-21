package com.crossover.airline.resource.output;

import java.util.ArrayList;
import java.util.List;

import com.crossover.airline.resource.BaseOutput;

public class SearchFlightOutput extends BaseOutput {

	private static final long serialVersionUID = -6993091719935158048L;

	private List<FlightOutput> onwardFlights = new ArrayList<>();
	
	private List<FlightOutput> returnFlights  = new ArrayList<>();

	public List<FlightOutput> getOnwardFlights() {
		return onwardFlights;
	}

	public void setOnwardFlights(List<FlightOutput> onwardFlights) {
		this.onwardFlights = onwardFlights;
	}

	public List<FlightOutput> getReturnFlights() {
		return returnFlights;
	}

	public void setReturnFlights(List<FlightOutput> returnFlights) {
		this.returnFlights = returnFlights;
	}
	
	public void addOnwardFlight(FlightOutput onwardFlight) {
		onwardFlights.add(onwardFlight);
	}
	
	public void addReturnFlight(FlightOutput returnFlight) {
		returnFlights.add(returnFlight);
	}
}
