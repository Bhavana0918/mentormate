package com.mentormate.mentormate.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.management.relation.RoleNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mentormate.mentormate.entities.PasswordResetToken;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.exceptions.UserAlreadyExistsException;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.repositories.TokenRepository;
import com.mentormate.mentormate.repositories.UserRepository;

import jakarta.validation.Valid;

//Service implementation for handling user-related operations
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository usersRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private TokenRepository tokenRepository;

	@Override
	public List<User> getAllUsers() {
		return usersRepository.findAll();
	}

	// Create a new user based on the provided user model
	// Throws RoleNotFoundException if the specified role is not found
	@Override
	public User createUsers(UserModel usersModel)
			throws SQLIntegrityConstraintViolationException, RoleNotFoundException {

		User existingUser = usersRepository.findByEmail(usersModel.getEmail());

		// Check if a user with the given email already exists
		if (existingUser != null) {
			logger.error("User already exists");
			throw new UserAlreadyExistsException("User already exists");
		}
		// Create a new Users entity using the data from the UsersModel
		@Valid
		User user = new User(usersModel.getEmail(), StringUtils.capitalize(usersModel.getFirstName()),
				usersModel.getLastName(), passwordEncoder.encode(usersModel.getPassword()), usersModel.getDesignation(),
				usersModel.getRoles());
		User savedUser = usersRepository.save(user);

		// adding information about current method into the logger object
		logger.debug("User created successfully with ID: {}", savedUser.getId());

		// Save the new user to the database
		return savedUser;
	}

	@Override
	public boolean isUserExists(String email) throws IllegalArgumentException {
		// Check if a user with the given email already exists in the database
		return usersRepository.findByEmail(email) != null;
	}

	@Override
	public User findByEmail(String username) throws IllegalArgumentException {

		// adding information about current method into the logger object
		logger.debug("finding user by username: {}", username);
		return usersRepository.findByEmail(username);
	}

	@Override
	public User getUserById(long userId) throws IllegalArgumentException {
		// adding information about current method into the logger object
		logger.debug("finding user by id: {}", userId);
		return usersRepository.getReferenceById(userId);
	}

	@Override
	public String sendChangePasswordEmail(User user) throws IllegalStateException, MailException {
		String passwordResetLink = generateResetToken(user);
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("mentormate.help@gmail.com");
		msg.setTo(user.getEmail());
		msg.setSubject("Reset Password");
		msg.setText("Hello \n\n" + "Please click on this link to Reset your Password :" + passwordResetLink + ". \n\n"
				+ "Regards \n" + "MentorMate");
		javaMailSender.send(msg);
		return "success";
	}

	@Override
	public boolean hasExipred(LocalDateTime expiryDateTime) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		return expiryDateTime.isAfter(currentDateTime);
	}

	@Override
	public String generateResetToken(User user) throws IllegalStateException {
		UUID uuid = UUID.randomUUID();
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);

		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setUser(user);
		passwordResetToken.setToken(uuid.toString());
		passwordResetToken.setExpiryDateTime(expiryDateTime);
		passwordResetToken.setUser(user);

		tokenRepository.save(passwordResetToken);

		String domainUrl = "http://localhost:8080/resetPassword";
		return domainUrl + "/" + passwordResetToken.getToken();
	}

	@Override
	public HashSet<User> getAllUsersExceptAdmin(String role){
		logger.debug("fetching all users except user with admin role");
		return usersRepository.findAllByRolesNotContains(role);
	}

	@Override
	public void updateUserRole(long userId, String role) throws DataAccessException{
		User userToBeUpdated = getUserById(userId);
		
		//if user doesn't already have given role then update roles
		if (!userToBeUpdated.getRoles().contains(role)) {
			logger.debug("Adding {} role for user with email {}", role, userToBeUpdated.getEmail());
			userToBeUpdated.getRoles().add(role);
			usersRepository.save(userToBeUpdated);
		}
	}
}
