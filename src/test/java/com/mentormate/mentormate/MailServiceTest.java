package com.mentormate.mentormate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.mentormate.mentormate.services.MailServiceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
 
@ExtendWith(MockitoExtension.class)
class MailServiceTest {
 
	@Mock
	private JavaMailSender mailSender;
 
	@InjectMocks
	private MailServiceImpl mailService;
 
	@Test
	void sendMailSuccess() throws MessagingException {
 
		String recipientEmail = "abcd@gamil.com";
		String subject = "Add Ratings";
		String htmlContent = "<html><body>Hello MentorMate</body></html>";
 
		// Mock the behavior to return a valid MimeMessage when createMimeMessage is
		// called
		MimeMessage mockMimeMessage = mock(MimeMessage.class);
		lenient().when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);
 
		mailService.sendMail(recipientEmail, subject, htmlContent);
 
		verify(mailSender, times(1)).createMimeMessage();
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}
 
}