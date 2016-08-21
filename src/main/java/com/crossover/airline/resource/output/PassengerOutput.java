package com.crossover.airline.resource.output;

import com.crossover.airline.entity.Passenger.Gender;
import com.crossover.airline.resource.BaseOutput;

public class PassengerOutput extends BaseOutput {

	private static final long serialVersionUID = -3725399288551752853L;

	private Long passengerId;
	
	private String name;
	
	private Gender gender;
	
	private int age;

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

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
