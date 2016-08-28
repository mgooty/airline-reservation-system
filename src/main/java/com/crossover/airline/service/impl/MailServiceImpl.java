package com.crossover.airline.service.impl;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.crossover.airline.service.MailService;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${send.from.email}")
	private String fromEmail;
	
	@Value("${email.subject.format}")
	private String subject;
	
	@Value("${email.body.format}")
	private String body;
	
	@Override
	public void emailTickets(String toEmail, String name, String attachmentPath) {
		sendEmail(toEmail, subject, String.format(body, name), attachmentPath);
	}

	private void sendEmail(String toEmail, String subject, String body, String attachmentPath) {
		MimeMessage message = mailSender.createMimeMessage();
		
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
		
			helper.setTo(toEmail);
			helper.setFrom(fromEmail);
			helper.setSubject(subject);
			helper.setText(body);
			
			FileSystemResource file = new FileSystemResource(new File(attachmentPath));
			helper.addAttachment(file.getFilename(), file);
			
			mailSender.send(message);
		} catch (Exception e) {
			// TODO log error message
			e.printStackTrace();
		}
	}
}
