package com.mentormate.mentormate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentormate.mentormate.entities.Objective;
import com.mentormate.mentormate.entities.User;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
	
	//fetch by user
	List<Objective> findAllByUser(User user);
}
