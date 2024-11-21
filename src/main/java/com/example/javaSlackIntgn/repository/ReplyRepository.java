package com.example.javaSlackIntgn.repository;

import com.example.javaSlackIntgn.model.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReplyRepository extends MongoRepository<Reply, String> {
    // Add custom query methods if needed
    List<Reply> findByChannelId(String channelId);
    List<Reply> findByThreadTs(String threadTs);
} 