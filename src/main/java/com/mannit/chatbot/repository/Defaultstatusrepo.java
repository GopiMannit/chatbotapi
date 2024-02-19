
package com.mannit.chatbot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mannit.chatbot.model.Defaultstatus;

public interface Defaultstatusrepo extends MongoRepository<Defaultstatus, String> {

}
