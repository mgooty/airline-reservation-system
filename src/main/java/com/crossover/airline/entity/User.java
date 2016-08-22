package com.crossover.airline.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User  extends BaseEntity {

	private static final long serialVersionUID = -2773377303146777457L;

	@Id
	private String email;
	
	@Column(name = "password", nullable = false)
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
