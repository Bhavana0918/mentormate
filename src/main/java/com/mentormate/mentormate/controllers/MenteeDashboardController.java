package com.mentormate.mentormate.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mentormate.mentormate.dto.OkrDTO;
import com.mentormate.mentormate.entities.Status;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.services.MentorMenteeRelationshipService;
import com.mentormate.mentormate.services.OKRService;
import com.mentormate.mentormate.services.UserService;

@Controller
public class MenteeDashboardController {

	private static final Logger logger = LoggerFactory.getLogger(MenteeDashboardController.class);

	@Autowired
	private UserService usersService;
	@Autowired
	private MentorMenteeRelationshipService mentorMenteeRelationshipService;
	@Autowired
	private OKRService okrService;

	@GetMapping("/mentee")
	public String menteeDashboard(Model model) {

		logger.info("Retrieving mentee dashboard");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserEmail = authentication.getName();

		// Fetching profile information
		User loggedInMentee = usersService.findByEmail(currentUserEmail);
		UserModel loggedInMenteeModel = new UserModel(loggedInMentee);
		// Fetching List of Mentees under a Mentor
		User mentor = mentorMenteeRelationshipService.getMentorForMentee(loggedInMentee);
		List<UserModel> listOfMentees = mentorMenteeRelationshipService.getMenteesWithSameMentor(loggedInMentee);

		// Fetching Okrs
		List<OkrDTO> listOfOkrs = okrService.getAllObjectivesAndKeyResultsForMentee(loggedInMentee);

		// using model to pass objects to View
		model.addAttribute("listOfOkrs", listOfOkrs);
		model.addAttribute("listOfMentees", listOfMentees);
		model.addAttribute("firstName", loggedInMentee.getFirstName());
		model.addAttribute("mentee", loggedInMenteeModel);
		if (mentor != null) {
			model.addAttribute("mentor", new UserModel(mentor));
		}

		if (loggedInMentee.getRoles().contains("MENTOR")) {
			model.addAttribute("hasMentorRole", true);
		}
		// Calculate average rating
		okrService.calculateAverageRatingForUser(loggedInMentee.getId());

		model.addAttribute("averageRating", loggedInMentee.getAverageRating());

		// Return the mentee dashboard view
		return "mentee";

	}

	@PostMapping("/updateKeyResultStatus/{keyResultId}")
	public String updateKeyResultStatus(@PathVariable Long keyResultId, @RequestParam("newStatus") Status newStatus,
			RedirectAttributes redirectAttributes) {

			logger.debug("Updating KeyResult Status. KeyResultId: {}, New Status: {}", keyResultId, newStatus);

			okrService.updateKeyResultStatus(keyResultId, newStatus);

			redirectAttributes.addFlashAttribute("successMessage", "KeyResult Status is now Updated !");

		return "redirect:/mentee";
	}
}
