package com.mentormate.mentormate.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.management.relation.RoleNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.mail.MessagingException;

@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<String> handleValidationException(SQLIntegrityConstraintViolationException e) {
		logger.error("Validation error occurred:", e);
		// Return a 400 Bad Request response with the error message
		return new ResponseEntity<>("Validation error occured. Please try again", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RoleNotFoundException.class)
	private ResponseEntity<String> handleRoleNotFound(RoleNotFoundException e) {
		logger.error("Role not found:");
		return new ResponseEntity<>("Provided role does not exist", HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	private ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
		logger.error("user already exists:");
		return new ResponseEntity<>("Username already exists. Please choose a different username.",
				HttpStatus.CONFLICT);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	private ResponseEntity<String> handleIllegalArgumentEx(IllegalArgumentException e) {
		logger.error("please pass a valid argument");
		return new ResponseEntity<>("please pass a valid argument", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MailException.class)
	private ResponseEntity<String> handleMailException(MailException e) {
		logger.error("An error occurred while sending the mail");
		return new ResponseEntity<>("An error occurred while sending the mail", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataAccessException.class)
	private ResponseEntity<String> handleDataAccessException(DataAccessException e) {
		logger.error("Database connection issue occurred");
		return new ResponseEntity<>(
				"An error occurred while communicating with the database. Requested operation could not be performed",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	private ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		logger.error("There is a violation of integrity contraint");
		return new ResponseEntity<>("There is a violation of integrity contraint. Kindly re-check your data",
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalStateException.class)
	private ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
		logger.error("IllegalStateException occurred:", e);
		if (e.getMessage().contains("Invalid user state")) {
			return new ResponseEntity<>("Incorect data provided.", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	private ResponseEntity<String> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
		logger.error("Requested data does not exist", e);
		return new ResponseEntity<>("Requested data does not exist.", HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MessagingException.class)
	private ResponseEntity<String> handleMessagingException(MessagingException e) {
		logger.error("Mail could not be sent to the user", e);
		return new ResponseEntity<>("Mail could not be sent", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
