package com.crossover.airline.service;

import com.crossover.airline.resource.input.CreditCardPaymentInput;
import com.crossover.airline.resource.output.BookingPaymentOutput;

public interface PaymentsService {

	BookingPaymentOutput processCreditCardPayment(CreditCardPaymentInput creditCardPaymentInput) throws Exception;
}
