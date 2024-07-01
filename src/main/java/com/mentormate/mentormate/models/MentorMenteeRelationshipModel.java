package com.mentormate.mentormate.models;

import com.mentormate.mentormate.entities.User;

import lombok.Getter;

@Getter
public class MentorMenteeRelationshipModel {

	private User mentor;
	private String mentorName;
	private String menteeName;
	private User mentee;

	public MentorMenteeRelationshipModel(User mentor, User mentee) {
		this.mentor = mentor;
		this.mentee = mentee;
		this.mentorName = mentor.getFirstName()+ " " + mentor.getLastName();
		this.menteeName = mentee.getFirstName()+ " " + mentee.getLastName();
	}

}