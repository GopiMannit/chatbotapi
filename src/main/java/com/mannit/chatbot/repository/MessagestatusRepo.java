
package com.mannit.chatbot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mannit.chatbot.model.SentMessageStatus;

public interface MessagestatusRepo extends MongoRepository<SentMessageStatus, String> {
	/*
	 * @Query("{Date:?0}") List<SentMessageStatus> findAllBydate(String date);
	 */
	
	@Query("{ 'Date' : { $regex: ?0, $options: 'i' } }")
	 List<SentMessageStatus> findAllBydate(String date);
	
	
}
