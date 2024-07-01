package com.mentormate.mentormate.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotNull(message = "email cannot be null")
	@Size(max = 50, message = "email length should be less than 50 chars")
	@Email(message = "email is not in right format")
	@Column(unique = true)
	private String email;
	@Column(nullable = false)
	@NotBlank(message = "first name cannot be null")
	@Size(max = 50, message = "firs name length should be less than 50 chars")
	private String firstName;
	@Column(nullable = false)
	@NotBlank(message = "last name cannot be null")
	@Size(max = 50, message = "last name length should be less than 50 chars")
	private String lastName;
	@Column(nullable = false)
	@NotBlank(message = "password cannot be null")
	private String password;
	@Size(max = 50, message = "last name length should be less than 50 chars")
	private String designation;
	@Column(columnDefinition = "double default 0")
	@DecimalMin("0.0")
	@DecimalMax("5.0")
    private Double averageRating;

	// Collection of roles associated with the user
	@NotNull(message = "role cannot be null")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"))
	@Column(name = "role", nullable=false) // Specify the column name for roles
	private List<String> roles;
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<PasswordResetToken> passwordResetToken; 


	public User(String email, String firstName, String lastName, String password, String designation,
			List<String> roles) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.designation = designation;
		this.roles = roles;
	}
	
	public User(String email, String firstName, String lastName, String password, String designation,
			Double averageRating, List<String> roles) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.designation = designation;
		this.averageRating = averageRating;
		this.roles = roles;
	}
	
	// Helper method to add a role to the roles list
	public void addRole(String role) {
		if (roles == null) {
			roles = new ArrayList<String>();
		}
		roles.add(role);
	}
	
}
