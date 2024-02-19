package com.mannit.chatbot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mannit.chatbot.model.QueriedPatient;


@Repository
public interface QuriedpRepo extends MongoRepository<QueriedPatient, String> {

	   @Query("{ 'timestamp' : { $regex: ?0, $options: 'i' } }")
	    List<QueriedPatient> findByDate(String date);
}
