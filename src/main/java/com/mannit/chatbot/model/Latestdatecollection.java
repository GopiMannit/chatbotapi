package com.mannit.chatbot.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "latestdatetime")
@Data
public class Latestdatecollection {
	
	@Id
	public String id ;
	
	public String rejected_lastupdated;
	
	public String appointment_lastupdated;
	
	public String queried_lastupdated;
	
	public LocalDateTime lastupdatedtime;

}
