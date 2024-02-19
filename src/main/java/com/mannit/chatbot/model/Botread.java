package com.mannit.chatbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="botread")
@Data
public class Botread {
	@Id
	private String id;
	private String phone_no_id;
	private String phone_number;
	private String status;
	private String wamid;
	private String timestamp;
}
