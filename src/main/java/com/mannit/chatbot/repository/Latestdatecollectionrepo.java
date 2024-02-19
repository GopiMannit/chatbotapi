package com.mannit.chatbot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mannit.chatbot.model.Latestdatecollection;


@Repository
public interface Latestdatecollectionrepo extends MongoRepository<Latestdatecollection, String>{

}
