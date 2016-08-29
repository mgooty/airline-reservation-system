package com.crossover.airline.ext.payment.gateway;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/paymentGateway")
public class PaymentGatewayController {

	@RequestMapping(method = RequestMethod.POST)
	public PaymentGatewayOutput processCreditCardPayment(@RequestBody PaymentGatewayInput input) {
		PaymentGatewayOutput output = new PaymentGatewayOutput();
		
		return output;
	}
}
