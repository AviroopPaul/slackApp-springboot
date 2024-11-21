package com.example.javaSlackIntgn.controller;

import com.example.javaSlackIntgn.model.Message;
import com.example.javaSlackIntgn.repository.MessageRepository;
import com.example.javaSlackIntgn.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private SlackService slackService;

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        try {
            slackService.sendMessage(message.getRecipient(), message.getText());
            return messageRepository.save(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Slack integration is working!";
    }
}
