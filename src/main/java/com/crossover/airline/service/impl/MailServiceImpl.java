package com.crossover.airline.service.impl;

import java.io.File;

import javax.mail.MessagingException;
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
	
	@Override
	public void sendMail(String subject, String toEmail, String body, String attachmentPath) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(toEmail);
		helper.setFrom(fromEmail);
		helper.setSubject(subject);
		helper.setText(body);
		
		FileSystemResource file = new FileSystemResource(new File(attachmentPath));
		helper.addAttachment(file.getFilename(), file);
		
		mailSender.send(message);
	}

}
