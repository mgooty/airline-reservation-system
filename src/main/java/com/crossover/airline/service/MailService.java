package com.crossover.airline.service;

import javax.mail.MessagingException;

public interface MailService {

	public void sendMail(String subject, String to, String body, String attachmentPath) throws MessagingException;
}
