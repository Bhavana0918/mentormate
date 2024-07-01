package com.mentormate.mentormate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentormate.mentormate.entities.MentorMenteeRelationship;
import com.mentormate.mentormate.entities.User;

@Repository
public interface MentorMenteeRelationshipRepository extends JpaRepository<MentorMenteeRelationship, Long> {
	// fetch record based on menteeId
	MentorMenteeRelationship findByMentee(User mentee);

	//fetch records based on mentor while excluding menteeToExclude (i.e currently logged in mentee)
	List<MentorMenteeRelationship> findByMentorAndMenteeNot(User mentor, User menteeToExclude);
	
	//fetch based on mentor
	List<MentorMenteeRelationship> findByMentor(User mentor);
	
	// fetch record based on menteeId and mentorId
	MentorMenteeRelationship findByMenteeAndMentor(User mentee, User mentor);

}
