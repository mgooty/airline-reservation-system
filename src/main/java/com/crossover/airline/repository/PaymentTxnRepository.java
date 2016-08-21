package com.crossover.airline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crossover.airline.entity.PaymentTxn;

@Repository
public interface PaymentTxnRepository extends JpaRepository<PaymentTxn, Long> {

}
