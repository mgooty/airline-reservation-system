package com.crossover.airline;

import static com.crossover.airline.common.Constants.PAYMENT_PROCESSED_QUEUE_NAME;
import static com.crossover.airline.common.Constants.TICKET_GENERATED_QUEUE_NAME;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableJpaAuditing
@SpringBootApplication
@EnableJms
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	@Bean
	public Queue paymentProcessedQueue() {
		return new ActiveMQQueue(PAYMENT_PROCESSED_QUEUE_NAME);
	}
	
	@Bean
	public Queue ticketGeneratedQueue() {
		return new ActiveMQQueue(TICKET_GENERATED_QUEUE_NAME);
	}
}
