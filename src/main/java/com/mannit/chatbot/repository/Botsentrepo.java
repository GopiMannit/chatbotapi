package com.mannit.chatbot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mannit.chatbot.model.Botsent;

public interface Botsentrepo extends MongoRepository<Botsent, String> {

}
