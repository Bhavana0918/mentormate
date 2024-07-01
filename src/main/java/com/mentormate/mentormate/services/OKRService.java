package com.mentormate.mentormate.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import com.mentormate.mentormate.dto.OkrDTO;
import com.mentormate.mentormate.entities.KeyResult;
import com.mentormate.mentormate.entities.Objective;
import com.mentormate.mentormate.entities.Status;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.ObjectiveModel;

public interface OKRService {

	/**
	 * Creates objectives for a user.
	 * 
	 * @param users     The user for whom the objective is created.
	 * @param objective The description of the objective.
	 * @return The created Objective.
	 * @throws SQLIntegrityConstraintViolationException
	 */
	Objective createObjectives(User users, String objective) throws SQLIntegrityConstraintViolationException;

	/**
	 * Updates an existing objective based on the provided ObjectivesModel.
	 * 
	 * @param objectiveModel The ObjectivesModel containing information to update
	 *                       the objective.
	 * @return The updated Objective.
	 */
	Objective updateObjective(ObjectiveModel objectiveModel);

	/**
	 * Retrieves an objective based on the specified ID.
	 * 
	 * @param id The unique identifier of the objective.
	 * @return The Objective associated with the given ID.
	 */
	Objective getObjective(long id);

	/**
	 * Retrieves objectives and key results for a mentee.
	 * 
	 * @param mentee The mentee for whom to retrieve objectives and key results.
	 * @return A List of OkrDTO representing objectives and key results for the
	 *         given mentee.
	 */
	List<OkrDTO> getAllObjectivesAndKeyResultsForMentee(User mentee);

	/**
	 * Creates a KeyResult for a specific Objective.
	 * 
	 * @param objectives The Objective for which to create the KeyResult.
	 * @param keyResult  The description of the KeyResult.
	 * @return The created KeyResult.
	 * @throws SQLIntegrityConstraintViolationException If there is a constraint violation like if Key-result is null
	 */
	KeyResult createKeyResults(Objective objectives, String keyResult) throws SQLIntegrityConstraintViolationException;

	/**
	 * Retrieves a KeyResult based on the specified ID.
	 * 
	 * @param id The unique identifier of the KeyResult.
	 * @return The KeyResult associated with the given ID.
	 */
	KeyResult getKeyResults(long id);

	/**
	 * 
	 * Retrieves a list of KeyResults associated with the specified Objective.
	 * 
	 * @param objective The Objective for which to retrieve KeyResults.
	 * @return A List of KeyResults associated with the given Objective.
	 */
	List<KeyResult> getAllKeyResultsForObjective(Objective objective);

	/**
	 * Saves a rating for the specified KeyResult.
	 * 
	 * @param keyResultId The unique identifier of the KeyResult.
	 * @param rating      The rating to be saved for the KeyResult.
	 */
	void saveRating(Long keyResultId, int rating);
	
	/**
	 * Saves a commet for the specified KeyResult.
	 * 
	 * @param keyResultId The unique identifier of the KeyResult.
	 * @param comment     The comment to be saved for the KeyResult.
	 */
	void saveComment(Long keyResultId, String comment);
	
	/**
	 * Calculates the average rating for the specified user.
	 * 
	 * @param userId The unique identifier of the user.
	 * @return The calculated average rating for the user.
	 */
	User calculateAverageRatingForUser(Long userId);

	/**
	 * Retrieves the mentee associated with the specified KeyResult.
	 * 
	 * @param keyResultId The unique identifier of the KeyResult.
	 * @return The mentee associated with the given KeyResult.
	 */
	User getMenteeByKeyResultId(Long keyResultId);
	
	/**
	 * Updates the status of a KeyResult identified by the provided keyResultId.
	 *
	 * @param keyResultId The ID of the KeyResult to be updated.
	 * @param newStatus   The new status to be set for the KeyResult.
	 */
	void updateKeyResultStatus(Long keyResultId, Status newStatus);

}