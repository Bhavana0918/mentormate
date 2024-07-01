package com.mentormate.mentormate.models;

import com.mentormate.mentormate.entities.KeyResult;
import com.mentormate.mentormate.entities.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyResultModel {
	private long id;
	private String keyResultDescription;
	private String comment;
	private int rating;
	private Status status;


	public KeyResultModel(KeyResult keyResult) {
		this.id = keyResult.getId();
		this.keyResultDescription = keyResult.getKeyResultDescription();
		this.comment = keyResult.getComment();
		this.rating=keyResult.getRating();
		this.status = keyResult.getStatus();

	}
}
