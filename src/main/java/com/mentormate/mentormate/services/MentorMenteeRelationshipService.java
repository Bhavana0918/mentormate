package com.mentormate.mentormate.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import com.mentormate.mentormate.entities.MentorMenteeRelationship;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.MentorMenteeRelationshipModel;
import com.mentormate.mentormate.models.UserModel;

public interface MentorMenteeRelationshipService {

	/**
	 * Retrieves the Mentor-Mentee relationship based on the specified ID.
	 * 
	 * @param id
	 * @return The MentorMenteeRelationship object associated with the given ID.
	 */
	MentorMenteeRelationship getMentorMenteeRelationship(long id);
	
	/**
	 * Retrieves the mentor associated with the specified mentee.
	 * 
	 * @param mentee The mentee for whom to find the mentor.
	 * @return The mentor User object for the given mentee.
	 */
	User getMentorForMentee(User mentee);

	/**
	 * Retrieves a list of UsersModel representing mentees sharing the same mentor
	 * as the specified mentee.
	 * 
	 * @param mentee The mentee for whom to find shared mentorship.
	 * @return A List of UsersModel representing mentees with the same mentor as the
	 *         given mentee.
	 */
	List<UserModel> getMenteesWithSameMentor(User mentee);

	/**
	 * Retrieves a list of mentee associated with the specified mentor.
	 * 
	 * @param mentor The mentor for whom to retrieve mentees.
	 * @return A List of mentee User objects associated with the given mentor.
	 */
	List<User> getMenteesForMentor(User mentor);
	
	/**
	 * Retrieves a list of MentorMenteeRelationshipModel representing all users with
	 * their mentor-mentee relationships.
	 * 
	 * @return A List of MentorMenteeRelationshipModel objects representing users
	 *         and their relationships.
	 */
	List<MentorMenteeRelationshipModel> getAllUsersWithRelationships();
	
	/**
	 * Sets a default mentor for a mentee
	 * @param mentee The mentee for whom default mentor is to be set
	 */
	void setDefaultMentorAndCreateMapping(User mentee);
	
	/**
	 * Updates the menor-mentee relationship
	 * @param mentor Selected mentor
	 * @param mentee Selected mentee
	 * @throws SQLIntegrityConstraintViolationException
	 */
	void updateMentorMenteeRelationship(User mentor, User mentee) throws SQLIntegrityConstraintViolationException;
	
	/**
	 * Checks if the mentor-mentee mapping exists 
	 * @param menteeId
	 * @param mentorId
	 * @return True if mentor-mentee mapping or its reverse is present or not
	 */
	boolean isMappingOrReverseMappingPresent(User mentee, User mentor);
	
	/**
	 * Gets mentor-mentee relationship based on menteeId and mentorId
	 * @param menteeId
	 * @param mentorId
	 * @return Mapping between mentee and mentor.
	 */
	MentorMenteeRelationship getRelationship(User mentee, User mentor);

}