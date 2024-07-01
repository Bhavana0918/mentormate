 package com.mentormate.mentormate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MentorMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentorMateApplication.class, args);
	}
	
}
