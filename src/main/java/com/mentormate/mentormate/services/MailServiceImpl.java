package com.mentormate.mentormate.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	@Autowired
	private JavaMailSender mailSender;

	@Async
	public void sendMail(String recipientEmail, String subject, String htmlContent) throws MessagingException {
		try {

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(recipientEmail);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);

			mailSender.send(mimeMessage);
			logger.debug("Email sent successfully to {}", recipientEmail);
		} catch (MessagingException e) {
			throw new MessagingException("Failed to send an email" + recipientEmail + ". Please try again later.", e);
		}
	}

}
