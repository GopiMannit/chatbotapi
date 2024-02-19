package com.mannit.chatbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="botsent")
@Data
public class Botsent {
	@Id
	private String id;
	private String phone_no_id;
	private String phone_number;
	private String status;
	private String wamid;
	private boolean bool_status;
	private String timestamp;
}
