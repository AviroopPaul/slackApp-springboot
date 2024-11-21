package com.example.javaSlackIntgn.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import jakarta.persistence.PrePersist;

@Document(collection = "replies")
public class Reply {
    @Id
    private String id;
    private String text;
    private String channelId;
    private String userId;
    private String threadTs;
    private String messageTs;
    private Date createdAt;
    private Boolean isBot;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThreadTs() {
        return threadTs;
    }

    public void setThreadTs(String threadTs) {
        this.threadTs = threadTs;
    }

    public String getMessageTs() {
        return messageTs;
    }

    public void setMessageTs(String messageTs) {
        this.messageTs = messageTs;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsBot() {
        return isBot;
    }

    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }
} 