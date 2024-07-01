package com.mentormate.mentormate.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mentormate.mentormate.dto.OkrDTO;
import com.mentormate.mentormate.entities.KeyResult;
import com.mentormate.mentormate.entities.Objective;
import com.mentormate.mentormate.entities.Status;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.KeyResultModel;
import com.mentormate.mentormate.models.ObjectiveModel;
import com.mentormate.mentormate.repositories.KeyResultRepository;
import com.mentormate.mentormate.repositories.ObjectiveRepository;
import com.mentormate.mentormate.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class OKRServiceImpl implements OKRService {

	private static final Logger logger = LoggerFactory.getLogger(OKRServiceImpl.class);

	@Autowired
	private ObjectiveRepository objectivesRepository;

	@Autowired
	private KeyResultRepository keyResultsRepository;

	@Autowired
	private UserRepository usersRepository;

	// Add a new objective
	@Override
	public Objective createObjectives(User user, String objective)
			throws DataAccessException, IllegalArgumentException {

		logger.debug("entering createObjectives method");

		// Validate input
		if (objective == null) {
			throw new IllegalArgumentException("User and objective cannot be null");
		}
		logger.debug("Creating objectives for user: {} (objective: {})", user.getEmail(), objective);
		Objective objectives = new Objective();
		objectives.setUser(user);
		objectives.setObjectiveDescription(objective);

		Objective savedObjectives = objectivesRepository.save(objectives);
		logger.debug("Objectives created successfully with ID: {}", savedObjectives.getId());
		return savedObjectives;
	}

	// update existing objective
	@Override
	public Objective updateObjective(ObjectiveModel objectiveModel)
			throws DataAccessException, IllegalArgumentException {
		return objectivesRepository
				.save(new Objective(objectiveModel.getUser(), objectiveModel.getObjectiveDescription()));
	}

	// Fetch objective based on id
	@Override
	public Objective getObjective(long id) throws IllegalArgumentException {
		// adding information about current method into the logger object
		logger.debug("getting objective based on id");
		return objectivesRepository.getReferenceById(id);
	}

	// Fetch all objectives assigned to a mentee
	@Override
	public List<OkrDTO> getAllObjectivesAndKeyResultsForMentee(User mentee)
			throws EmptyResultDataAccessException, DataAccessException {

		logger.debug("Retrieving all objectives and key results for mentee: {}", mentee.getEmail());

		List<Objective> objectives = objectivesRepository.findAllByUser(mentee);
		List<OkrDTO> listOfOkrs = new ArrayList<>();

		for (Objective objective : objectives) {
			listOfOkrs.add(new OkrDTO(objective.getObjectiveDescription(),
					getAllKeyResultsForObjective(objective).stream().map(KeyResultModel::new).toList()));
		}

		logger.debug("Retrieved {} OKRs for mentee: {}", listOfOkrs.size(), mentee.getEmail());

		return listOfOkrs; // Return the list of OKR DTOs

	}

	// Add a new KeyResult
	@Override
	public KeyResult createKeyResults(Objective objectives, String keyResult)
			throws SQLIntegrityConstraintViolationException, DataAccessException {
		KeyResult keyResults = new KeyResult();
		keyResults.setObjective(objectives);
		keyResults.setKeyResultDescription(keyResult);
		keyResults.setStatus(Status.NOT_STARTED);

		
		logger.debug("key-result created");
		return keyResultsRepository.save(keyResults);
	}

	// Fetch KeyResult based on id
	@Override
	public KeyResult getKeyResults(long id) throws EmptyResultDataAccessException, DataAccessException {

		// adding information about current method into the logger object
		logger.debug("getting key-result based on id: {}", id);
		return keyResultsRepository.getReferenceById(id);
	}

	// Fetch all key-results for an objective
	@Override
	public List<KeyResult> getAllKeyResultsForObjective(Objective objective) throws DataAccessException {
		if (objective != null) {
			// adding information about fetching key-results for an objective
			logger.debug("getting all key-results for objective {}", objective.getObjectiveDescription());

			return keyResultsRepository.findAllByObjective(objective);
		} else {
			return Collections.emptyList();
		}
	}

	// To add a rating for a KeyResult
	@Override
	public void saveRating(Long keyResultId, int rating) throws DataAccessException, IllegalArgumentException {
		// Retrieve the KeyResult from the repository by its ID
		KeyResult keyResult = keyResultsRepository.findById(keyResultId)
				.orElseThrow(() -> new EntityNotFoundException("KeyResult not found with id: " + keyResultId));

		// Set the new rating for the KeyResult
		keyResult.setRating(rating);

		// Save the updated KeyResult back to the repository
		keyResultsRepository.save(keyResult);

		// Log a message indicating the successful addition of the rating
		logger.debug("Rating added successfully for KeyResult ID: {}, New Rating: {}", keyResultId, rating);
	}

	@Override
	public void saveComment(Long keyResultId, String comment) throws DataAccessException, IllegalArgumentException {
		try {
			// Retrieve the KeyResult from the repository by its ID
			KeyResult keyResult = keyResultsRepository.findById(keyResultId)
					.orElseThrow(() -> new EntityNotFoundException("KeyResult not found with id: " + keyResultId));

			// Set the new rating for the KeyResult
			keyResult.setComment(comment);

			// Save the updated KeyResult back to the repository
			keyResultsRepository.save(keyResult);

			// Log a message indicating the successful addition of the rating
			logger.debug("Comment added successfully for KeyResult ID: {}, New Comment: {}", keyResultId, comment);
		} catch (EntityNotFoundException e) {
			// Log an error if the KeyResult with the specified ID is not found
			logger.error("Failed to add comment. KeyResult not found with id: {}", keyResultId);
			throw e;
		}
	}

	@Override
	public User calculateAverageRatingForUser(Long userId) throws DataAccessException, IllegalArgumentException {
		User user = usersRepository.getReferenceById(userId);
		List<KeyResult> userKeyResults = keyResultsRepository.findByObjective_User(user);

		logger.debug("Calculating and saving average rating for user: {}", user.getId());
		double averageRating = userKeyResults.stream().mapToDouble(KeyResult::getRating).average().orElse(0.0);
		user.setAverageRating(averageRating);

		return usersRepository.save(user);
	}

	@Override
	public User getMenteeByKeyResultId(Long keyResultId) throws IllegalArgumentException {
		// Find the KeyResult by ID
		Optional<KeyResult> keyResultOptional = keyResultsRepository.findById(keyResultId);

		// Check if the KeyResult exists
		if (keyResultOptional.isPresent()) {
			KeyResult keyResult = keyResultOptional.get();

			return keyResult.getObjective().getUser();

		} else {
			// Handle the case where the KeyResult is not found
			throw new EntityNotFoundException("KeyResult not found with ID: " + keyResultId);
		}
	}

	//Updates the status of a KeyResult identified by the provided keyResultId.
	@Override
	public void updateKeyResultStatus(Long keyResultId, Status newStatus) {
			KeyResult keyResult = keyResultsRepository.findById(keyResultId)
					.orElseThrow(() -> new EntityNotFoundException("KeyResult not found with id: " + keyResultId));

			keyResult.setStatus(newStatus);

			keyResultsRepository.save(keyResult);
	}

}
