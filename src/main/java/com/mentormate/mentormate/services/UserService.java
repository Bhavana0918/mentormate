package com.mentormate.mentormate.services;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import javax.management.relation.RoleNotFoundException;

import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;

import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.UserModel;

public interface UserService {

	/**
	 * Retrieves a list of all users 
	 * 
	 * @return A List of User objects representing all users in the database.
	 */
	List<User> getAllUsers();

	/**
	 * Creates user based on the provided UsersModel.
	 * 
	 * @param usersModel The UsersModel containing information to create user.
	 * @return The created User object.
	 * @throws SQLIntegrityConstraintViolationException If there is a violation of
     * integrity constraints, such as a duplicate user.
	 * @throws RoleNotFoundException If a specified role in the UsersModel is not found.
	 */
	User createUsers(UserModel usersModel) throws SQLIntegrityConstraintViolationException, RoleNotFoundException;
	
	/**
	 * Updates the role of a user in the system.
	 *
	 * @param usersId The unique identifier of the user whose role is to be updated.
	 * @param role The new role to be assigned to the user.
	 * @throws DataAccessException If there is an issue accessing or updating the user's role in the database.
	 */
	void updateUserRole(long usersId, String role) throws DataAccessException;
	
	/**
	 * Checks if a user with the specified email exists in the system.
	 * 
	 * @param email The email address of user to check for existence.
	 * @return {@code true} if a user with the given email exists; otherwise, {@code false}.
	 * @throws IllegalArgumentException If the provided email is null or empty.
	 */
	boolean isUserExists(String email) throws IllegalArgumentException;

	/**
	 * Finds a user by their email address.
	 * 
	 * @param username The email address of the user to find.
	 * @return The User object associated with the given email.
	 * @throws IllegalArgumentException If the provided email is null or empty.
	 */
	User findByEmail(String username) throws IllegalArgumentException;

	/**
	 * Retrieves a user by their Id.
	 * 
	 * @param userId The unique identifier of the user to retrieve.
	 * @return The User object associated with the given user ID.
	 * @throws IllegalArgument If the provided user ID is not valid 
	 */
	User getUserById(long userId) throws IllegalArgumentException;

	/**
	 * Sends a change password email to the specified user.
	 * 
	 * @param user The User object for whom to send the change password email.
	 * @return A status message indicating the success or failure of the email sending process.
	 * @throws MailException If there is an error during the email sending process.
	 * @throws IllegalStateException If the email sending operation is attempted in an invalid state.
	 */
	String sendChangePasswordEmail(User user) throws MailException, IllegalStateException;

	/**
	 * Checks if the specified expiration date and time has already occurred.
	 * 
	 * @param expiryDateTime The date and time representing the expiration point.
	 * @return {@code true} if the expiration has occurred; otherwise, {@code false}.
	 */
	boolean hasExipred(LocalDateTime expiryDateTime);
	

	/**
	 * Generates a reset token for the specified user.
	 * 
	 * @param user The User object for whom to generate the reset token.
	 * @return The generated reset token as a String.
	 * @throws IllegalStateException If the operation is attempted in an invalid state.
	 */
	String generateResetToken(User user) throws IllegalStateException;
	
	/**
	 * Retrieves HashSet of all users except Admin.
	 * @param role 
	 * @return
	 * @throws DataAccessException
	 */
	HashSet<User> getAllUsersExceptAdmin(String role);
	
}
