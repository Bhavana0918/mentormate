 package com.mentormate.mentormate.configuration;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private UserService usersService;

	private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);

	// Method called on successful authentication
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// Get the user from details
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();

		// Fetch the user details from the database using the username
		User user = usersService.findByEmail(username);

		// Log authentication success
		logger.info("Authentication successful for user: {}", user.getEmail());

		// Get the authorities (roles) granted to the authenticated user
		var authourities = authentication.getAuthorities();

		List<String> roles = authourities.stream().map(r -> r.getAuthority()).toList();

		// Redirect the user based on their role
		if (roles.stream().anyMatch(role -> role.equals("MENTOR"))) {
		    // Redirect to the mentor dashboard
		    response.sendRedirect("/mentor");
		} else if (roles.stream().anyMatch(role -> role.equals("MENTEE"))) {
		    // Redirect to the mentee dashboard
		    response.sendRedirect("/mentee");
		} else if (roles.stream().anyMatch(role -> role.equals("ADMIN"))) {
		    // Redirect to the admin dashboard
		    response.sendRedirect("/admin?firstName=" + user.getFirstName() + "&userId=" + user.getId());
		} else {
		    response.sendRedirect("/error");
		}

	}

}
