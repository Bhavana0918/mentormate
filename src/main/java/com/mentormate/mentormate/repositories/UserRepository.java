package com.mentormate.mentormate.repositories;

import java.util.HashSet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mentormate.mentormate.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	// Custom query method to find a user by email
	User findByEmail(String email);
	
	@Query("SELECT u " +
		       "FROM User u " +
		       "WHERE NOT EXISTS (SELECT 1 FROM MentorMenteeRelationship mm WHERE u = mm.mentee)")
	HashSet<User> getAllUsersWithNoMentor();
	
	HashSet<User> findAllByRolesNotContains(String role);
	
}
