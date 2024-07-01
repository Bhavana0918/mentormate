package com.mentormate.mentormate.services;

import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public interface MailService {

	/**
	 * Sends an email with the specified details.
	 * 
	 * @param mail    The recipient's email address.
	 * @param subject The subject of the email.
	 * @param message The content of the email message .
	 * @throws MessagingException If an error occurs during the email sending
	 *                            process.
	 */
	public void sendMail(String mail, String subject, String message) throws MessagingException;

}
