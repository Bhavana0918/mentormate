package com.mentormate.mentormate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import com.mentormate.mentormate.entities.MentorMenteeRelationship;
import com.mentormate.mentormate.entities.User;
import com.mentormate.mentormate.models.MentorMenteeRelationshipModel;
import com.mentormate.mentormate.models.UserModel;
import com.mentormate.mentormate.repositories.MentorMenteeRelationshipRepository;
import com.mentormate.mentormate.repositories.UserRepository;
import com.mentormate.mentormate.services.MentorMenteeRelationshipServiceImpl;
import com.mentormate.mentormate.services.UserService;
import com.mentormate.mentormate.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class MentorMenteeRelationshipServiceImplTest {

	@Mock
	MentorMenteeRelationshipRepository mentorMenteeRelationshipRepo;

	@InjectMocks
	MentorMenteeRelationshipServiceImpl mentorMenteeRelationshipService;

	@Mock
	UserRepository userRepository;
	
	@Mock
	UserServiceImpl userService;

	MentorMenteeRelationship expectedRelationship = new MentorMenteeRelationship();
	User mentor;
	User mentee;

	@BeforeEach
	void setUp() {
		mentor = new User("sample@gmail.com", "mentor", "test", "Abcd@123", "Sample Designation", List.of("MENTOR"));
		mentor.setId(1);
		mentee = new User("sample22@gmail.com", "mentee", "test", "Abcd@123", "Sample Designation", List.of("MENTEE"));
		mentee.setId(2);
		expectedRelationship.setId(1);
		expectedRelationship.setMentor(mentor);
		expectedRelationship.setMentee(mentee);
	}

	@Test
	void getMentorMenteeRelationship_ReturnsRelationship() {
		long id = 1L;
		when(mentorMenteeRelationshipRepo.getReferenceById(anyLong())).thenReturn(expectedRelationship);

		MentorMenteeRelationship returnedRelationship = mentorMenteeRelationshipService.getMentorMenteeRelationship(id);

		assertSame(expectedRelationship, returnedRelationship);
	}

	@Test
	void getMentorMenteeRelationshipById_ReturnsNullWhenNotFound() {
		long id = 1L;
		when(mentorMenteeRelationshipRepo.getReferenceById(id)).thenReturn(null);

		MentorMenteeRelationship returnedRelationship = mentorMenteeRelationshipService.getMentorMenteeRelationship(id);

		assertNull(returnedRelationship);
	}

	@Test
    void getMentorForMentee_WhenMappingExists_ReturnsMapping() {
        when(mentorMenteeRelationshipRepo.findByMentee(any(User.class))).thenReturn(expectedRelationship);

        User returnedMentor = mentorMenteeRelationshipService.getMentorForMentee(mentee);

        assertSame(expectedRelationship.getMentor(), returnedMentor);
    }

	@Test
    void getMentorForMentee_WhenNoMappingExists_ReturnsNull() {
        when(mentorMenteeRelationshipRepo.findByMentee(any(User.class))).thenReturn(null);

        User returnedMentor = mentorMenteeRelationshipService.getMentorForMentee(mentee);

        assertNull(returnedMentor);
    }

	@Test
	void testGetMenteesForMentor() throws Exception {
		User mentee2 = new User("sample22@gmail.com", "mentee", "test", "Abcd@123", "Sample Designation",
				List.of("MENTEE"));
		mentee2.setId(2);
		MentorMenteeRelationship expectedRelationship2 = new MentorMenteeRelationship(mentor, mentee2);
		when(mentorMenteeRelationshipRepo.findByMentor(mentor)).thenReturn(List.of(expectedRelationship2));

		List<User> mentees = mentorMenteeRelationshipService.getMenteesForMentor(mentor);

		assertEquals(1, mentees.size());
		verify(mentorMenteeRelationshipRepo).findByMentor(mentor);
	}


	@Test
	void testGetAllUsersWithRelationships() {
		User mentor = new User("mentor@example.com", "Mentor", "User", "password", "Mentor", new ArrayList<>());
		User mentee = new User("mentee@example.com", "Mentee", "User", "password", "Mentee", new ArrayList<>());
		MentorMenteeRelationship relationship = new MentorMenteeRelationship(mentor, mentee);
		List<MentorMenteeRelationship> relationshipsList = List.of(relationship);

		when(mentorMenteeRelationshipRepo.findAll()).thenReturn(relationshipsList);

		List<MentorMenteeRelationshipModel> result = mentorMenteeRelationshipService.getAllUsersWithRelationships();

		assertEquals(1, result.size());
		MentorMenteeRelationshipModel model = result.get(0);
		assertEquals("Mentor User", model.getMentorName());
		assertEquals("Mentee User", model.getMenteeName());
	}

	@Test
	void testcreateMentorMenteeRelationship_throwsIllegalArgumentExceptionException()
			throws SQLIntegrityConstraintViolationException, DataAccessException {
		assertThrows(IllegalArgumentException.class,
				() -> mentorMenteeRelationshipService.updateMentorMenteeRelationship(null, mentee));
	}

	@Test
	void setDefaultMentor() {
		User mentee = new User(); 
		User defaultMentor = new User(); 

		when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(defaultMentor);
		mentorMenteeRelationshipService.setDefaultMentorAndCreateMapping(mentee);

		verify(mentorMenteeRelationshipRepo, times(1)).save(any(MentorMenteeRelationship.class));
	}
	
	@Test
    void updateMentorMenteeRelationship() throws SQLIntegrityConstraintViolationException {
        User mentor = new User(); 
        User mentee = new User(); 
        MentorMenteeRelationship relationship = new MentorMenteeRelationship(mentor, mentee);

        when(mentorMenteeRelationshipRepo.findByMentee(mentee)).thenReturn(relationship);
        mentorMenteeRelationshipService.updateMentorMenteeRelationship(mentor, mentee);

        verify(mentorMenteeRelationshipRepo, times(1)).findByMentee(mentee);
        assertEquals(mentor, relationship.getMentor());
    }

    @Test
    void isMappingOrReverseMappingPresent() {
        User mentor = new User(); 
        User mentee = new User(); 

        when(mentorMenteeRelationshipRepo.findByMenteeAndMentor(mentee, mentor)).thenReturn(null);
        when(mentorMenteeRelationshipRepo.findByMenteeAndMentor(mentor, mentee)).thenReturn(null);

        boolean result = mentorMenteeRelationshipService.isMappingOrReverseMappingPresent(mentee, mentor);
        assertFalse(result);

        when(mentorMenteeRelationshipRepo.findByMenteeAndMentor(mentee, mentor)).thenReturn(new MentorMenteeRelationship(mentor, mentee));

        result = mentorMenteeRelationshipService.isMappingOrReverseMappingPresent(mentee, mentor);
        assertTrue(result);
    }

    @Test
    void getRelationship() {
        User mentor = new User(); 
        User mentee = new User();
        MentorMenteeRelationship relationship = new MentorMenteeRelationship(mentor, mentee);

        when(mentorMenteeRelationshipRepo.findByMenteeAndMentor(mentee, mentor)).thenReturn(relationship);

        MentorMenteeRelationship result = mentorMenteeRelationshipService.getRelationship(mentee, mentor);
        assertEquals(relationship, result);
    }

}