package com.crossover.airline.resource.input;

import com.crossover.airline.resource.BaseInput;

public class FlightInput extends BaseInput {

	private static final long serialVersionUID = 2576250957399290555L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
