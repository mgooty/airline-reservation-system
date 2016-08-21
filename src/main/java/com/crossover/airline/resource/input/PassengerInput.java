package com.crossover.airline.resource.input;

import com.crossover.airline.entity.Passenger.Gender;
import com.crossover.airline.resource.BaseInput;

public class PassengerInput extends BaseInput {
	
	private static final long serialVersionUID = -9009424154660973723L;

	private String name;
	
	private Gender gender;
	
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
