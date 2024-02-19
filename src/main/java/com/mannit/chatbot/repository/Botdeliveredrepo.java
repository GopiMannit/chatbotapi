package com.mannit.chatbot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mannit.chatbot.model.Botdelivered;

public interface Botdeliveredrepo extends MongoRepository<Botdelivered, String> {

}
