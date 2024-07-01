package com.mentormate.mentormate.controllers;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.management.relation.RoleNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.mentormate.mentormate.entities.PasswordResetToken;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.repositories.TokenRepository;
import com.mentormate.mentormate.repositories.UserRepository;
import com.mentormate.mentormate.services.MailService;
import com.mentormate.mentormate.services.MentorMenteeRelationshipService;
import com.mentormate.mentormate.services.UserService;

import jakarta.mail.MessagingException;

@Controller
public class RegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private UserService usersService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository usersRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private MailService mailService;
	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
	private MentorMenteeRelationshipService mentorMenteeRelationshipService;

	
	// Handle the GET request to show the home page
	@GetMapping("/home")
	public String home() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "home";
		}
		return "redirect:/logout";
		
	}

	// Handle the GET request to show the about-us page
	@GetMapping("/about-us")
	public String aboutUs() {
		return "about-us";
	}

	// Handle the GET request to show the contact-us page
	@GetMapping("/contact-us")
	public String contactUs() {
		return "contact-us";
	}

// Handle the POST request for user registration
	@PostMapping("/registration")
	public String registerUser(@ModelAttribute("user") UserModel usersModel, Model model)
			throws SQLIntegrityConstraintViolationException, MessagingException, RoleNotFoundException {

		logger.info("Registering user");
		try {
			// Check if the user with the given email already exists
			if (usersService.isUserExists(usersModel.getEmail())) {
				model.addAttribute("error", "User with this email already exists.");
				return "registration";
			} else {

				// Create a new user based on the provided user model
				User createdUser = usersService.createUsers(usersModel);
				logger.debug("user created successfully");

				if (createdUser != null) {

					// Generate email content by processing the Thymeleaf template
					Context userRegistrationContext = new Context();
					userRegistrationContext.setVariable("firstName", createdUser.getFirstName());

					// Send the registration email with the processed content
					String emailContent = templateEngine.process("registration-email", userRegistrationContext);
					logger.debug("Generated email content: {}", emailContent);
					
					mentorMenteeRelationshipService.setDefaultMentorAndCreateMapping(createdUser);

					// Send the email once user registered
					mailService.sendMail(createdUser.getEmail(), "Welcome to MentorMate!", emailContent);
					logger.debug("Registration email sent successfully to the email address: {}",
							createdUser.getEmail());

				}

				model.addAttribute("message", "Registration successful! You can now log in.");

				// Redirect to the login page after successful registration
				return "registration";
			}

		} catch (RuntimeException e) {
			logger.error("Registration failed: {}", e.getMessage());
			e.printStackTrace();

			model.addAttribute("error", "An error occurred during registration. Please try again.");

			// Return to the registration page with the error message
			return "registration";
		}
	}

// Handle the GET request to show the registration form
	@GetMapping("/registration")
	public String showRegistrationForm(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			// Add an empty user model to be populated in the form
			model.addAttribute("user", new UserModel());
			return "registration";
		}
		return "redirect:/logout";
	}

	// Handle the GET request to show the login form
	@GetMapping("/login")
	public String showLoginForm() {
		// Return the login page view
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		return "redirect:/logout";
	}

	// Handle the GET request to show the forgot password form
	@GetMapping("/forgotPassword")
	public String forgotPassword() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "forgotPassword";
		}
		return "redirect:/logout";
	}

	// Handle the POST request for forgot password
	@PostMapping("/forgotPassword")
	public String forgotPassordProcess(@ModelAttribute UserModel userModel)
			throws MailException, IllegalStateException {
		String output = "";
		User user = usersRepository.findByEmail(userModel.getEmail());
		if (user != null) {
			output = usersService.sendChangePasswordEmail(user);
		}
		if (output.equals("success")) {
			return "redirect:/forgotPassword?success";
		}
		return "redirect:/login?error";
	}

	// Handle the GET request to show the reset password form
	@GetMapping("/resetPassword/{token}")
	public String resetPasswordForm(@PathVariable String token, Model model) {
		PasswordResetToken reset = tokenRepository.findByToken(token);
		if (reset != null && usersService.hasExipred(reset.getExpiryDateTime())) {
			model.addAttribute("email", reset.getUser().getEmail());
			return "resetPassword";
		}
		return "redirect:/forgotPassword?error";
	}

	// Handle the POST request for reset password
	@PostMapping("/resetPassword")
	public String passwordResetProcess(@ModelAttribute UserModel userModel) {
		User user = usersRepository.findByEmail(userModel.getEmail());
		if (user != null) {
			user.setPassword(passwordEncoder.encode(userModel.getPassword()));
			usersRepository.save(user);
		}
		return "redirect:/login";
	}

}
