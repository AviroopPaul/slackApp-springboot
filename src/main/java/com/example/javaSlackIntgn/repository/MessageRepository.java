package com.example.javaSlackIntgn.repository;

import com.example.javaSlackIntgn.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
}
