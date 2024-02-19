package com.mannit.chatbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "smartyuppiesresponse")
public class Smartyupresp {

	@Id
	private String id;

	private String name;

	private String phone_number;

	private String msg;

	private String timestamp;
	
	private String wamid;
	
	private String phone_number_id;
	
	private String lastupdateddate;
	
	
}
