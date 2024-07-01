package com.mentormate.mentormate.controllers;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.mentormate.mentormate.dto.OkrDTO;
import com.mentormate.mentormate.entities.Objective;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.services.MailService;
import com.mentormate.mentormate.services.MentorMenteeRelationshipService;
import com.mentormate.mentormate.services.OKRService;
import com.mentormate.mentormate.services.UserService;

import jakarta.mail.MessagingException;

@Controller
public class MentorDashboardController {

	private static final Logger logger = LoggerFactory.getLogger(MentorDashboardController.class);

	@Autowired
	private MentorMenteeRelationshipService mentorMenteeRelationshipService;

	@Autowired
	private UserService usersService;

	@Autowired
	private OKRService okrService;

	@Autowired
	private MailService mailService;

	@Autowired
	private TemplateEngine templateEngine;

	@GetMapping("/mentor")
	public String mentorDashboard(Model model) {
		logger.info("Retrieving mentor dashboard");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();

			User currentUser = usersService.findByEmail(currentUserName);
			model.addAttribute("firstName", currentUser.getFirstName());
			model.addAttribute("userId", currentUser.getId());
			model.addAttribute("mentor", currentUser);

			User loggedInMentor = usersService.getUserById(currentUser.getId());
			UserModel loggedInMentorModel = new UserModel(loggedInMentor);
			model.addAttribute("mentee", loggedInMentorModel);

			List<UserModel> mentees = mentorMenteeRelationshipService.getMenteesForMentor(currentUser).stream()
					.map(UserModel::new).toList();
			model.addAttribute("mentees", mentees);
			if (loggedInMentor.getRoles().contains("MENTEE")) {
				model.addAttribute("hasMenteeRole", true);
			}

		}
		return "mentor";
	}

	// Get mentee details by mentee ID
	@GetMapping("/mentor/mentee/{menteeId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> viewMenteeDetails(@PathVariable Long menteeId) {

		logger.debug("Retrieving mentee details for mentee ID: {}", menteeId);
		// Find the mentee by ID
		User mentee = usersService.getUserById(menteeId);
		// Fetching Okrs
		List<OkrDTO> listOfOkrs = okrService.getAllObjectivesAndKeyResultsForMentee(mentee);
		// Check if the mentee exists
		if (mentee != null) {
			// Return mentee details as JSON
			Map<String, Object> menteeDetails = new HashMap<>();
			menteeDetails.put("firstName", mentee.getFirstName());
			menteeDetails.put("lastName", mentee.getLastName());
			menteeDetails.put("email", mentee.getEmail());
			menteeDetails.put("designation", mentee.getDesignation());

			// Calculating average rating
			okrService.calculateAverageRatingForUser(mentee.getId());

			menteeDetails.put("averageRating", mentee.getAverageRating());
			menteeDetails.put("objectives", listOfOkrs);

			return ResponseEntity.ok(menteeDetails);
		} else {
			// Handle the case where mentee is not found
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// save the objectives and key-results entered in the form
	@PostMapping("/save")
	public String saveOKR(@ModelAttribute("objective") String objective,
			@ModelAttribute("keyResult1") String keyResult1, @ModelAttribute("keyResult2") String keyResult2,
			Long menteeId) throws SQLIntegrityConstraintViolationException, MessagingException {

		logger.debug("Saving OKRs for mentee ID: {}", menteeId);

		Objective savedObjective = okrService.createObjectives(usersService.getUserById(menteeId), objective);
		okrService.createKeyResults(savedObjective, keyResult1);
		okrService.createKeyResults(savedObjective, keyResult2);
		logger.debug("OKRs saved successfully");

		// Send mail to mentee
		User mentee = usersService.getUserById(menteeId);

		if (mentee != null) {
			// Generate email content by processing the Thymeleaf template
			Context objectivesAndKeyResultsMail = new Context();
			objectivesAndKeyResultsMail.setVariable("firstName", mentee.getFirstName());

			// Process the objectives-email template
			String emailContent = templateEngine.process("objectives-email", objectivesAndKeyResultsMail);

			// Send the email to mentee when mentor added objective and keyresults
			mailService.sendMail(mentee.getEmail(), "Objective and Key Results Added", emailContent);
			logger.debug("Objective and key results added email sent successfully to {}", mentee.getEmail());

		}
		return "redirect:/mentor";
	}

	// To handle adding ratings for a key result
	@PostMapping("/mentor/mentee/{keyResultId}/addratings")
	public String addRatings(@PathVariable Long keyResultId, @RequestParam int rating) throws MessagingException {
		logger.debug("Adding rating for key result ID: {}", keyResultId);
		okrService.saveRating(keyResultId, rating);
		logger.debug("Rating added successfully");

		// Retrieve mentee details based on keyResultId
		User mentee = okrService.getMenteeByKeyResultId(keyResultId);

		// Send notification email to mentee
		if (mentee != null) {
			// Prepare Thymeleaf context
			Context addRatingEmailContext = new Context();
			addRatingEmailContext.setVariable("firstName", mentee.getFirstName());

			// Process the Thymeleaf template
			String emailContent = templateEngine.process("email-for-rating", addRatingEmailContext);

			// Send the email
			mailService.sendMail(mentee.getEmail(), "Rating Added", emailContent);
			logger.debug("Rating added email sent successfully to {}", mentee.getEmail());

		}

		return "redirect:/mentor";
	}

	@PostMapping("/mentor/mentee/{keyResultId}/addcomment")
	public String addComment(@PathVariable Long keyResultId, @RequestParam String comment) throws MessagingException {
		logger.debug("Adding comment for key result ID: {}", keyResultId);
		okrService.saveComment(keyResultId, comment);
		logger.debug("Comment added successfully");

		// Retrieve mentee details based on keyResultId
		User mentee = okrService.getMenteeByKeyResultId(keyResultId);

		// Send notification email to mentee
		if (mentee != null) {
			// Prepare Thymeleaf context
			Context thymeleafContext = new Context();
			thymeleafContext.setVariable("firstName", mentee.getFirstName());

			// Process the Thymeleaf template
			String emailContent = templateEngine.process("email-for-comments", thymeleafContext);

			// Send the email
			mailService.sendMail(mentee.getEmail(), "Comment Added for Keyresults", emailContent);
			logger.debug("Comment added email sent successfully to {}", mentee.getEmail());
		}
		return "redirect:/mentor";
	}
}
