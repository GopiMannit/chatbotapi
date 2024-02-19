package com.mannit.chatbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "CurrentPatients")
@Data
public class CurrentPatient {

	@Id
	private String id;

	private String name;

	private String phone_number;

	private String Doctor_choice;

	private String timestamp;
	
	private String msg;
	
	private String wamid;
	
	private String phone_number_id;


}
