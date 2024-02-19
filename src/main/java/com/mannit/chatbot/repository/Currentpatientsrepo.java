package com.mannit.chatbot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mannit.chatbot.model.CurrentPatient;

@Repository
public interface Currentpatientsrepo extends MongoRepository<CurrentPatient, String> {
	   @Query("{ 'timestamp' : { $regex: ?0, $options: 'i' } }")
	    List<CurrentPatient> findByDate(String date);
	   
	   
	   @Query("{ 'timestamp' : { $regex: ?0, $options: 'i' },'phone_number': ?1 } }")
	    List<CurrentPatient> findByDateAndPhonenumber(String date,String phone_number);
	
	    
	   
}
