package com.mentormate.mentormate.controllers;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.MentorMenteeRelationshipModel;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.repositories.MentorMenteeRelationshipRepository;
import com.mentormate.mentormate.services.MentorMenteeRelationshipService;
import com.mentormate.mentormate.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminDashboardController {

	@Autowired
	UserService usersService;
	@Autowired
	MentorMenteeRelationshipService mentorMenteeRelationshipService;
	@Autowired
	MentorMenteeRelationshipRepository mentorMenteeRelationshipRepository;

	private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

	@GetMapping("/admin")
	public String adminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		logger.info("Retrieving admin dashboard");
		// Retrieve admin information
		UserModel admin = new UserModel(usersService.findByEmail(userDetails.getUsername()));
		model.addAttribute("firstName", admin.getFirstName());

		// Retrieve mentor-mentee relationships
		List<MentorMenteeRelationshipModel> relationships = mentorMenteeRelationshipService
				.getAllUsersWithRelationships();
		model.addAttribute("mentorMenteeMappings", relationships);

		model.addAttribute("mentorData", usersService.getAllUsersExceptAdmin("ADMIN"));
		model.addAttribute("menteeData", usersService.getAllUsersExceptAdmin("ADMIN"));
		return "admin";
	}

	@PostMapping("/saveMapping")
	public String saveNewMapping(@RequestParam("mentor") String mentor, @RequestParam("mentee") String mentee,
			HttpSession session) throws SQLIntegrityConstraintViolationException {

		User selectedMentee = usersService.getUserById(Long.parseLong(mentee));
		User selectedMentor = usersService.getUserById(Long.parseLong(mentor));

		// Check for existing and reverse mappings early
		if (mentorMenteeRelationshipService.isMappingOrReverseMappingPresent(selectedMentee, selectedMentor)) {
			logger.info("Mapping or reverse mapping already exists");
			session.setAttribute("errorMessage", "This mentor-mentee mapping or its reverse already exists.");
			return "redirect:/admin";
		}

		logger.debug("Creating new mentor-mentee mapping with mentor: {} and mentee: {}", selectedMentor.getEmail(),
				selectedMentee.getEmail());

		// fetch mentor's mentor
		User selectedMentorsMentor = mentorMenteeRelationshipService.getMentorForMentee(selectedMentor);

		// save mapping if the selectedMentee doesn't have a mentor or if reverse
		// mapping doesn't exist
		if (!Objects.equals(selectedMentorsMentor != null ? selectedMentorsMentor.getEmail() : null,
				selectedMentee.getEmail())) {
			logger.info("Saving new mentor-mentee mapping");

			// save new mapping
			mentorMenteeRelationshipService.updateMentorMenteeRelationship(selectedMentor, selectedMentee);
			session.setAttribute("successMessage", "Mentor-mentee mapping updated successfully!");

			// updates mentor's and mentee's roles
			usersService.updateUserRole(selectedMentee.getId(), "MENTEE");
			usersService.updateUserRole(selectedMentor.getId(), "MENTOR");
			session.setAttribute("errorMessage", " ");
		} else {
			logger.info("Failed to save new mapping. Reversed mapping already exists");

			session.setAttribute("successMessage", " ");

			// add error message
			session.setAttribute("errorMessage", "Reversed mapping already exists");
		}

		return "redirect:/admin";
	}

}