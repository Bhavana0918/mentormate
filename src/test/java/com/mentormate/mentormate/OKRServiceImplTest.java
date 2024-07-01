package com.mentormate.mentormate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import com.mentormate.mentormate.dto.OkrDTO;
import com.mentormate.mentormate.entities.KeyResult;
import com.mentormate.mentormate.entities.Objective;
import com.mentormate.mentormate.entities.Status;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.repositories.KeyResultRepository;
import com.mentormate.mentormate.repositories.ObjectiveRepository;
import com.mentormate.mentormate.repositories.UserRepository;
import com.mentormate.mentormate.services.OKRServiceImpl;
import com.mentormate.mentormate.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class OKRServiceImplTest {

	@Mock
	ObjectiveRepository objectivesRepository;

	@InjectMocks
	OKRServiceImpl okrService;

	@Mock
	KeyResultRepository keyResultsRepository;

	@Mock
	UserRepository usersRepository;

	@InjectMocks
	UserServiceImpl usersService;
	
	@Mock
	OkrDTO okrDTO;

	@Test
	void testCreateObjective() throws DataAccessException, IllegalArgumentException {
		List<String> roles = new ArrayList<>();
		roles.add("mentor");
		User user = new User("google@gmail.com", "jim", "halpert", "hello", "manager", roles);

		String objective = "Learn new programming language";

		Objective expectedObjective = new Objective();
		expectedObjective.setId(1);
		expectedObjective.setUser(user);
		expectedObjective.setObjectiveDescription(objective);

		when(objectivesRepository.save(any(Objective.class))).thenReturn(expectedObjective);

		Objective savedObjectives = okrService.createObjectives(user, objective);

		assertThrows(IllegalArgumentException.class, () -> okrService.createObjectives(user, null));
		assertEquals(expectedObjective, savedObjectives);
	}

	@Test
	void testGetObjective() throws IllegalArgumentException {
		long id = 1;
		Objective expectedObjective = new Objective();
		expectedObjective.setId(id);
		expectedObjective.setObjectiveDescription("Testing objective 1");

		when(objectivesRepository.getReferenceById(id)).thenReturn(expectedObjective);

		Objective retrievedObjective = okrService.getObjective(id);
		assertEquals(expectedObjective, retrievedObjective);
	}

	@Test
	void testCreateKeyResult() throws SQLIntegrityConstraintViolationException, DataAccessException {
		List<String> roles = new ArrayList<>();
		roles.add("mentor");
		User user = new User("google@gmail.com", "jim", "halpert", "hello", "manager", roles);
		Objective objectives = new Objective();
		objectives.setUser(user);

		long id = 1;
		String keyResult = "key result 1";
		KeyResult expectedKeyResults = new KeyResult();
		expectedKeyResults.setId(id);
		expectedKeyResults.setObjective(objectives);
		expectedKeyResults.setKeyResultDescription(keyResult);

		when(keyResultsRepository.save(any(KeyResult.class))).thenReturn(expectedKeyResults);
		KeyResult savedKeyResults = okrService.createKeyResults(objectives, keyResult);
		assertEquals(expectedKeyResults, savedKeyResults);
	}

	@Test
	void testGetKeyResult() {
		long id = 1;
		KeyResult expectedKeyResult = new KeyResult();
		expectedKeyResult.setId(id);
		expectedKeyResult.setKeyResultDescription("testing key-result 1");
		when(keyResultsRepository.getReferenceById(id)).thenReturn(expectedKeyResult);

		KeyResult retrievedKeyResult = okrService.getKeyResults(id);
		assertEquals(expectedKeyResult, retrievedKeyResult);
	}

	@Test
	void testGetAllKeyResultsForObjective() {
		// Set up mock data
		Objective objective = new Objective();
		objective.setObjectiveDescription("Test Objective");

		List<KeyResult> expectedKeyResults = List.of(new KeyResult(objective, "Key Result 1"),
				new KeyResult(objective, "Key Result 2"));

		when(keyResultsRepository.findAllByObjective(objective)).thenReturn(expectedKeyResults);

		List<KeyResult> retrievedKeyResults = okrService.getAllKeyResultsForObjective(objective);

		assertEquals(expectedKeyResults, retrievedKeyResults);
	}

	@Test
	void testGetAllKeyResultsForObjective_isEmpty() throws DataAccessException {
		// Call the method with null objective
		List<KeyResult> retrievedKeyResults = okrService.getAllKeyResultsForObjective(null);

		// Verify that an empty list is returned
		assertTrue(retrievedKeyResults.isEmpty());
	}

	@Test
	void testGetMenteeByKeyResultId() throws IllegalArgumentException {

		Long id = 1L;
		List<String> roles = new ArrayList<>();
		roles.add("mentor");
		User mentee = new User("google@gmail.com", "jim", "halpert", "hello", "manager", roles);
		Objective objective = new Objective();
		objective.setObjectiveDescription("Test Objective");
		objective.setUser(mentee);
		
		KeyResult keyResult1 = new KeyResult(objective, "Key Result 1");
		keyResult1.setObjective(objective);

		when(keyResultsRepository.findById(id)).thenReturn(Optional.of(keyResult1));

		User retrievedMentee = okrService.getMenteeByKeyResultId(id);

		assertEquals(mentee, retrievedMentee);
	}

	@Test
	void testSaveComment() {
		// Arrange
		Long keyResultId = 1L;
		String comment = "This is a test comment";

		KeyResult keyResult = new KeyResult();
		when(keyResultsRepository.findById(keyResultId)).thenReturn(Optional.of(keyResult));

		// Act
		okrService.saveComment(keyResultId, comment);

		// Assert
		verify(keyResultsRepository, times(1)).findById(keyResultId);
		verify(keyResultsRepository, times(1)).save(keyResult);
		assertEquals(comment, keyResult.getComment());
	}

	@Test
	void testAddRating() {
		// Arrange
		Long keyResultId = 1L;
		int rating = 5;

		KeyResult keyResult = new KeyResult();
		when(keyResultsRepository.findById(keyResultId)).thenReturn(Optional.of(keyResult));

		// Act
		okrService.saveRating(keyResultId, rating);

		// Assert
		verify(keyResultsRepository, times(1)).findById(keyResultId);
		verify(keyResultsRepository, times(1)).save(keyResult);
		assertEquals(rating, keyResult.getRating());
	}

	@Test
        void getAllObjectivesAndKeyResultsForMentee() throws Exception {
		
		List<String> roles = new ArrayList<>();
		roles.add("mentor");
		User user = new User("google@gmail.com", "jim", "halpert", "hello", "manager", roles);
        Objective objective1 = new Objective(user,"Objective 1");
        Objective objective2 = new Objective(user,"Objective 2");
        
        List<KeyResult> keyResults1 = List.of(new KeyResult(objective1,"Key Result 1.1"));
        List<KeyResult> keyResults2 = List.of(new KeyResult(objective2,"Key Result 2.1"));
        List<Objective> objectives = List.of(objective1, objective2);

        when(objectivesRepository.findAllByUser(user)).thenReturn(objectives);
        when(keyResultsRepository.findAllByObjective(objectives.get(0))).thenReturn(keyResults1);
        when(keyResultsRepository.findAllByObjective(objectives.get(1))).thenReturn(keyResults2);

        // Call the method under test
        List<OkrDTO> okrDtos = okrService.getAllObjectivesAndKeyResultsForMentee(user);

        // Verify interactions and results
        verify(objectivesRepository, times(1)).findAllByUser(user);
        verify(keyResultsRepository, times(2)).findAllByObjective(any()); // Assuming getAllKeyResultsForObjective calls it

        assertEquals("Objective 1", okrDtos.get(0).getObjective());
        assertEquals("Objective 2", okrDtos.get(1).getObjective());
    }
	
	@Test
    void testUpdateKeyResultStatus() {
        Long keyResultId = 1L;
        Status newStatus = Status.IN_PROGRESS;

        KeyResult mockKeyResult = new KeyResult();
        when(keyResultsRepository.findById(keyResultId)).thenReturn(Optional.of(mockKeyResult));
        when(keyResultsRepository.save(any())).thenReturn(mockKeyResult);

        okrService.updateKeyResultStatus(keyResultId, newStatus);

        verify(keyResultsRepository).findById(keyResultId);
        verify(keyResultsRepository).save(mockKeyResult);

    }
}
