package com.mentormate.mentormate.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mentorMenteeRelationship")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MentorMenteeRelationship {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "mentor cannot be null")
	@ManyToOne(targetEntity = User.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "mentorId", referencedColumnName = "id")
	private User mentor;

	@NotNull(message = "mentee cannot be null")
	@ManyToOne(targetEntity = User.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "menteeId", referencedColumnName = "id")
	private User mentee;

	public MentorMenteeRelationship(User mentor, User mentee) {
		this.mentor = mentor;
		this.mentee = mentee;
	}

}