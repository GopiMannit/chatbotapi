package com.mannit.chatbot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mannit.chatbot.model.Noappointment;

@Repository
public interface Noappointmentrepo extends MongoRepository<Noappointment, String> {

	   @Query("{ 'timestamp' : { $regex: ?0, $options: 'i' } }")
	    List<Noappointment> findByDate(String date);
	
}
