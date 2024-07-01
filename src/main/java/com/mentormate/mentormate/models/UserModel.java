package com.mentormate.mentormate.models;

import java.util.List;

import com.mentormate.mentormate.entities.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserModel {
	private long id;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String confirmPassword;
	private String designation;
	private List<String> roles;
	private Double averageRating;

	public UserModel(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.designation = user.getDesignation();
		this.roles = user.getRoles();
		this.averageRating = user.getAverageRating();
	}

}
