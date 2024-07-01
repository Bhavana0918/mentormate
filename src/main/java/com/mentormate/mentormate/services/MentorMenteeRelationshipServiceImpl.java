package com.mentormate.mentormate.services;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mentormate.mentormate.entities.MentorMenteeRelationship;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.MentorMenteeRelationshipModel;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.repositories.MentorMenteeRelationshipRepository;
import com.mentormate.mentormate.repositories.UserRepository;

import jakarta.validation.Valid;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class MentorMenteeRelationshipServiceImpl implements MentorMenteeRelationshipService {

	private static final Logger logger = LoggerFactory.getLogger(MentorMenteeRelationshipServiceImpl.class);

	@Autowired
	private MentorMenteeRelationshipRepository mentorMenteeRelationshipRepo;
	
	@Autowired
	private UserRepository userRepository;

	// Returns one mentee-mentor mapping based on id
	@Override
	public MentorMenteeRelationship getMentorMenteeRelationship(long id)
			throws EmptyResultDataAccessException, DataAccessException {
		return mentorMenteeRelationshipRepo.getReferenceById(id);
	}

	// Returns a mentor (user object) based on mentee's user Id. Assuming a mentee
	// has only one mentor
	@Override
	public User getMentorForMentee(User mentee) throws EmptyResultDataAccessException, DataAccessException {
		MentorMenteeRelationship menteemapping = mentorMenteeRelationshipRepo.findByMentee(mentee);
		if (menteemapping != null) {
			logger.debug("Mnetor-mentee mapping found: {}", menteemapping);
			logger.debug("Mentor found for mentee: {}", mentee.getEmail());
			return menteemapping.getMentor();
		} else {
			logger.error("No mentor found for mentee: {}, ID: {}", mentee.getFirstName(), mentee.getId());
			return null;
		}
	}

	@Override
	public List<UserModel> getMenteesWithSameMentor(User mentee)
			throws EmptyResultDataAccessException, DataAccessException {
		User mentor = getMentorForMentee(mentee);
		if (mentor != null) {
			// fetches list of records based on mentor. Using stream, getMentee() getter is
			// called for each element, returns a List<Users> containing mentees
			return mentorMenteeRelationshipRepo.findByMentorAndMenteeNot(mentor, mentee).stream()
					.map(m -> new UserModel(m.getMentee())).toList();
		} else {
			// adding information about current method into the logger object
			logger.error("No mentor found for mentee: {}", mentee.getEmail());
			return Collections.emptyList();
		}
	}

	@Override
	public List<User> getMenteesForMentor(User mentor) throws EmptyResultDataAccessException, DataAccessException {

		// adding information about current method into the logger object
		logger.debug("Retrieving mentees for mentor: {}, ID: {}", mentor.getFirstName(), mentor.getId());
		return mentorMenteeRelationshipRepo.findByMentor(mentor).stream().map(m -> m.getMentee()).toList();
	}

	@Override
	public List<MentorMenteeRelationshipModel> getAllUsersWithRelationships()
			throws EmptyResultDataAccessException, DataAccessException {

		// Fetch all mentor-mentee relationships from the database
		List<MentorMenteeRelationship> mentorMenteeMapping = mentorMenteeRelationshipRepo.findAll();
		logger.debug("Fetched {} mentor-mentee relationships from the database", mentorMenteeMapping.size());
		return mentorMenteeMapping.stream().map(m -> new MentorMenteeRelationshipModel(m.getMentor(), m.getMentee()))
				.toList();
	}

	@Override
	public void setDefaultMentorAndCreateMapping(User mentee) {
		User defaultMentor = userRepository.findByEmail("NotAkhilsmail@gmail.com");
		logger.debug("Setting a default mentor");
		MentorMenteeRelationship defaultRelationship = new MentorMenteeRelationship(defaultMentor, mentee);
		mentorMenteeRelationshipRepo.save(defaultRelationship);
	}

	@Override
	public void updateMentorMenteeRelationship(User mentor, User mentee) {
		MentorMenteeRelationship updatedMentorMenteeRelationship = mentorMenteeRelationshipRepo.findByMentee(mentee);
		logger.debug("Updating mentor-mentee relationship");
		updatedMentorMenteeRelationship.setMentor(mentor);
		logger.debug("Mentor-Mentee Mapping updated successfully with ID: {}", updatedMentorMenteeRelationship.getId());
	}

	@Override
	public boolean isMappingOrReverseMappingPresent(User mentee, User mentor) {
		return getRelationship(mentee, mentor) != null ||
				getRelationship(mentor, mentee) != null;
	}

	@Override
	public MentorMenteeRelationship getRelationship(User mentee, User mentor) {
		logger.debug("fetching mentor-mentee relationship");
		return mentorMenteeRelationshipRepo.findByMenteeAndMentor(mentee, mentor);
	}
	
}
