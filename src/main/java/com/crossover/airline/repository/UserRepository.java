package com.crossover.airline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crossover.airline.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	public User findByEmailAndPassword(String email, String password);
}
