package com.mentormate.mentormate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentormate.mentormate.entities.KeyResult;
import com.mentormate.mentormate.entities.Objective;
import com.mentormate.mentormate.entities.User;

@Repository
public interface KeyResultRepository extends JpaRepository<KeyResult, Long> {
	
	//fetch record based on objective
	List<KeyResult> findAllByObjective(Objective objective);

	//fetch key-results based on objective where user associated with objective is equal to the user provided. 
	List<KeyResult> findByObjective_User(User user);
}
