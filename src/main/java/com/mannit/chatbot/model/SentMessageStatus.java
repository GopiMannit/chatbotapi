
package com.mannit.chatbot.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Data;

@Document(collection = "messagestatus")
@Data
public class SentMessageStatus {

	@Id
	private String id;
	private String phone_number;
	private String date;


}
