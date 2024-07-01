package com.mentormate.mentormate.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "keyResults")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KeyResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull(message = "objective cannot be null")
	@ManyToOne(targetEntity = Objective.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "objectivesId", referencedColumnName = "id")
	private Objective objective;
	@Lob
	@Column(nullable = false)
	@NotBlank(message = "Key-result cannot be blank")
	private String keyResultDescription;
	@Lob
	private String comment;
	@Min(0)
	@Max(5)
	private int rating;
	@Enumerated(EnumType.STRING)
	private Status status;

	public KeyResult(Objective objective, String keyResultDescription) {
		this.objective = objective;
		this.keyResultDescription = keyResultDescription;
	}

	public KeyResult(Objective objective, String keyResultDescription, String comment, int rating) {
		this.objective = objective;
		this.keyResultDescription = keyResultDescription;
		this.comment = comment;
		this.rating = rating;
	}
	
	

}
