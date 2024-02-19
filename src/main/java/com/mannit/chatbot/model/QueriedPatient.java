package com.mannit.chatbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "QuriedPatients")
public class QueriedPatient {

	@Id
	private String id;

	private String name;

	private String phone_number;

	private String timestamp;
	
	private String wamid;
	
	private String phone_number_id;

}
