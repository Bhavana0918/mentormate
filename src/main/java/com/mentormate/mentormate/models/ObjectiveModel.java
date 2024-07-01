package com.mentormate.mentormate.models;

import com.mentormate.mentormate.entities.Objective;
import com.mentormate.mentormate.entities.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectiveModel {
	private long id;
	private User user;
	private String objectiveDescription;
	
	public ObjectiveModel(Objective objective) {
		this.id = objective.getId();
		this.user = objective.getUser();
		this.objectiveDescription = objective.getObjectiveDescription();
	}
}
