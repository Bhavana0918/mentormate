package com.mentormate.mentormate.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "objectives")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Objective {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "user cannot be null")
	@ManyToOne(targetEntity = User.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private User user;
	@Column(nullable = false)
	@NotBlank(message = "objective cannot be null")
	@Size(max = 500, message = "objective length should be less than 500 chars")
	private String objectiveDescription;

	public Objective(User user, String objectiveDescription) {
		this.user = user;
		this.objectiveDescription = objectiveDescription;
	}

}
