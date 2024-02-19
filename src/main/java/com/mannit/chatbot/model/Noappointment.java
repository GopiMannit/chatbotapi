package com.mannit.chatbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Noappointment")
public class Noappointment {
	@Id
	private String id;

	private String name;

	private String phone_number;

	private String timestamp;
	
	private String wamid;
	
	private String phone_number_id;
}
