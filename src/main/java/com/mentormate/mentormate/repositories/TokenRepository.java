package com.mentormate.mentormate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentormate.mentormate.entities.PasswordResetToken;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Integer> {
	PasswordResetToken findByToken(String token) ;
}
