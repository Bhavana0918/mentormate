package com.mentormate.mentormate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.management.relation.RoleNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mentormate.mentormate.entities.PasswordResetToken;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.exceptions.UserAlreadyExistsException;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.repositories.TokenRepository;
import com.mentormate.mentormate.repositories.UserRepository;
import com.mentormate.mentormate.services.MailService;
import com.mentormate.mentormate.services.MentorMenteeRelationshipService;
import com.mentormate.mentormate.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

	@Mock
	private UserRepository usersRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private MailService mailService;

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private JavaMailSender javaMailSenderMock;

	@Mock
	private TokenRepository tokenRepository;

	@Mock
	private MentorMenteeRelationshipService mentorMenteeRelationshipService;

	// Test for getAllUsers method
	@Test
	void testGetAllUsers() {
		// Create a list of test users
		User user1 = new User("ranjita@gmail.com", "Ranjita", "Hegde", "password", "Tester", new ArrayList<>());
		user1.setId(1);

		User user2 = new User("rachita@gmail.com", "Rachita", "Lastname", "password", "Tester", new ArrayList<>());
		user2.setId(2);

		List<User> mockUsersList = Arrays.asList(user1, user2);

		when(usersRepository.findAll()).thenReturn(mockUsersList);

		// Call the getAllUsers method
		List<User> actualUsers = userService.getAllUsers();

		assertEquals(mockUsersList, actualUsers);
	}

	// Test for isUserExists method when the user exists
	@Test
	void testIsUserExistsWhenUserExists() {
		User user1 = new User("ranjita@gmail.com", "Ranjita", "Hegde", "password", "Tester", new ArrayList<>());
		user1.setId(1);
		when(usersRepository.findByEmail("ranjita@gmail.com")).thenReturn(user1);

		// Call the isUserExists method with an existing email
		boolean userExists = userService.isUserExists("ranjita@gmail.com");

		assertTrue(userExists);
	}

	// Test for isUserExists method when the user does not exist
	@Test
        void testIsUserExistsWhenUserDoesNotExist() {
            when(usersRepository.findByEmail("nonexistentuser@gmail.com")).thenReturn(null);

            // Call the isUserExists method with a non-existing email
            boolean userExists = userService.isUserExists("nonexistentuser@gmail.com");

            assertFalse(userExists);
        }

	// Test for createUsers method
	@Test
	void testCreateUsers() throws RoleNotFoundException, SQLIntegrityConstraintViolationException {
		// Creating a test UsersModel
		UserModel testUserModel = new UserModel();
		testUserModel.setEmail("ranjita@gmail.com");
		testUserModel.setFirstName("Ranjita");
		testUserModel.setLastName("Hegde");
		testUserModel.setPassword("Ranjita@1234");
		testUserModel.setDesignation("Tester");

		when(usersRepository.findByEmail(("ranjita@gmail.com"))).thenReturn(null);

		when(usersRepository.save(any())).thenAnswer(invocation -> {
			User savedUser = invocation.getArgument(0);
			savedUser.setId(1); // sample ID for the saved user
			return savedUser;
		});
		when(passwordEncoder.encode(("Ranjita@1234"))).thenReturn("encodedPassword");

		// Call the createUsers method
		User createdUser = userService.createUsers(testUserModel);

		// Verifying that the save method was called with the correct user
		assertNotNull(createdUser);
		assertEquals("Ranjita", createdUser.getFirstName());
		assertEquals("encodedPassword", createdUser.getPassword());
	}

	@Test
	void testFindByEmail() {
		String userEmail = "abc@example.com";
		User expectedUser = new User();
		expectedUser.setEmail(userEmail);

		when(usersRepository.findByEmail(userEmail)).thenReturn(expectedUser);

		User actualUser = userService.findByEmail(userEmail);

		assertEquals(expectedUser, actualUser);

		verify(usersRepository, times(1)).findByEmail(userEmail);
	}

	@Test
	void testGetUserById() {
		long userId = 1L;
		User expectedUser = new User();
		expectedUser.setId(userId);

		when(usersRepository.getReferenceById(userId)).thenReturn(expectedUser);

		User actualUser = userService.getUserById(userId);

		assertEquals(expectedUser, actualUser);

		// Verifying that the repository's getReferenceById method was called with the
		// correct ID
		verify(usersRepository, times(1)).getReferenceById(userId);
	}

	@Test
	void testCreateUsers_UserAlreadyExists() {
		UserModel usersModel = new UserModel();
		usersModel.setEmail("existing@gmail.com");

		User existingUser = new User();
		existingUser.setEmail("existing@gmail.com");

		when(usersRepository.findByEmail("existing@gmail.com")).thenReturn(existingUser);

		assertThrows(UserAlreadyExistsException.class, () -> {
			userService.createUsers(usersModel);
		});

		// Verifying that findByEmail method was called with the correct email
		verify(usersRepository, times(1)).findByEmail("existing@gmail.com");

		// Verifying that save method was not called
		verify(usersRepository, never()).save(any());
	}

	@Test
	void testHasExpired() {

		// Create an instance of UsersServiceImpl with the fixed clock and mocked
		UserServiceImpl usersService = new UserServiceImpl();

		// Test a scenario where expiryDateTime is after the fixed date
		LocalDateTime futureDateTime = LocalDateTime.parse("2022-01-01T00:00:00");
		assertFalse(usersService.hasExipred(futureDateTime));

	}

	@Test
	void testGenerateResetToken() {

		when(tokenRepository.save(any())).thenReturn(new PasswordResetToken());
	
		User user = new User(); 
		String result = userService.generateResetToken(user);
		
		assertEquals("http://localhost:8080/resetPassword/", result.substring(0, 36)); 
		
		verify(tokenRepository).save(any());
	}

	@Test
	void testSendEmail() throws IllegalStateException, Exception {
		
		User user = new User();
		user.setEmail("test@example.com");
		lenient().when(userService.generateResetToken(user)).thenReturn("mockResetToken");
		
		String result = userService.sendChangePasswordEmail(user);
		
		assertEquals("success", result);

	}

	@Test
	void testGetAllUsersExceptAdmin() {
		String role = "admin";
		HashSet<User> users = new HashSet<>();
		when(usersRepository.findAllByRolesNotContains(role)).thenReturn(users);

		HashSet<User> result = userService.getAllUsersExceptAdmin(role);
		verify(usersRepository, times(1)).findAllByRolesNotContains(role);
		assertEquals(users, result);
	}

	@Test
	void updateUserRole_AddsNewRole() throws DataAccessException {
		// Set up mock user
		long userId = 1L;
		String role = "ADMIN";
		User mockUser = mock(User.class);
		when(mockUser.getRoles()).thenReturn(new ArrayList<String>()); // Start with an empty set of roles
		when(mockUser.getEmail()).thenReturn("user@example.com");
		when(usersRepository.getReferenceById(userId)).thenReturn(mockUser);

		userService.updateUserRole(userId, role);

		List<String> expectedRoles = new ArrayList<String>();
		expectedRoles.add(role);
		assertEquals(expectedRoles, mockUser.getRoles());
		verify(usersRepository).save(mockUser);
	}

	@Test
	void updateUserRole_DoesNotAddExistingRole() throws DataAccessException {
		long userId = 1L;
		String role = "ADMIN";
		User mockUser = mock(User.class);
		List<String> initialRoles = new ArrayList<String>();
		initialRoles.add(role);
		mockUser.addRole(role);
		when(mockUser.getRoles()).thenReturn(initialRoles);
		when(usersRepository.getReferenceById(userId)).thenReturn(mockUser);

		userService.updateUserRole(userId, role);

		verify(usersRepository, never()).save(any());
	}
}
