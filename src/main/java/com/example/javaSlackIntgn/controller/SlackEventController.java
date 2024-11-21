package com.example.javaSlackIntgn.controller;

import com.example.javaSlackIntgn.service.SlackService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.model.event.AppMentionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.javaSlackIntgn.model.Reply;
import com.example.javaSlackIntgn.repository.ReplyRepository;

@RestController
@RequestMapping("/api/slack")
public class SlackEventController {

    private static final Logger logger = LoggerFactory.getLogger(SlackEventController.class);

    @Autowired
    private SlackService slackService;

    @Autowired
    private ReplyRepository replyRepository;

    @PostMapping("/events")
    public ResponseEntity<?> handleSlackEvent(@RequestBody String jsonPayload) {
        try {
            // Log the incoming payload
            logger.info("Received Slack event payload: {}", jsonPayload);

            // Use regular Gson instead of snake case for initial parsing
            JsonObject jsonObject = new Gson().fromJson(jsonPayload, JsonObject.class);

            // Handle URL verification
            if (jsonObject.has("type") && jsonObject.get("type").getAsString().equals("url_verification")) {
                JsonObject response = new JsonObject();
                response.addProperty("challenge", jsonObject.get("challenge").getAsString());
                return ResponseEntity.ok(response.toString());
            }

            // Check if it's an event_callback
            if (jsonObject.has("type") && jsonObject.get("type").getAsString().equals("event_callback")) {
                JsonObject eventObject = jsonObject.getAsJsonObject("event");
                
                logger.info("Event object: {}", eventObject);

                if (eventObject != null && eventObject.has("type") 
                    && eventObject.get("type").getAsString().equals("app_mention")) {
                    
                    // Create AppMentionEvent using constructor
                    AppMentionEvent mentionEvent = new AppMentionEvent();
                    mentionEvent.setText(eventObject.get("text").getAsString());
                    mentionEvent.setTs(eventObject.get("ts").getAsString());
                    mentionEvent.setChannel(eventObject.get("channel").getAsString());
                    
                    // Save user's message to database
                    Reply userReply = new Reply();
                    userReply.setText(mentionEvent.getText());
                    userReply.setChannelId(mentionEvent.getChannel());
                    userReply.setMessageTs(mentionEvent.getTs());
                    userReply.setIsBot(false);  // Indicate this is a user message
                    
                    if (eventObject.has("thread_ts")) {
                        userReply.setThreadTs(eventObject.get("thread_ts").getAsString());
                    }
                    
                    if (eventObject.has("user")) {
                        userReply.setUserId(eventObject.get("user").getAsString());
                    }
                    
                    replyRepository.save(userReply);
                    logger.info("Stored user reply in database: {}", userReply);
                    
                    // Process the mention event and get bot's response
                    String botResponse = slackService.handleMention(mentionEvent);
                    
                    // Save bot's response to database
                    Reply botReply = new Reply();
                    botReply.setText(botResponse);
                    botReply.setChannelId(mentionEvent.getChannel());
                    botReply.setMessageTs(mentionEvent.getTs());  // You might want to update this with actual bot message timestamp
                    botReply.setIsBot(true);  // Indicate this is a bot message
                    botReply.setThreadTs(mentionEvent.getTs());  // Set thread_ts to create thread with original message
                    botReply.setUserId("BOT");  // Or your bot's actual user ID
                    
                    replyRepository.save(botReply);
                    logger.info("Stored bot reply in database: {}", botReply);
                }
            }

            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            logger.error("Error processing Slack event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }
}