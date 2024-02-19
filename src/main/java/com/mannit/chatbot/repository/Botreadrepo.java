package com.mannit.chatbot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mannit.chatbot.model.Botread;

public interface Botreadrepo extends MongoRepository<Botread, String> {

}
