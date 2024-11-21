package com.example.javaSlackIntgn.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppMentionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SlackService {

    private static final Logger log = LoggerFactory.getLogger(SlackService.class);

    @Value("${slack.bot.token}")
    private String slackToken;

    private static final List<String> GREETINGS = Arrays.asList(
        "hi", "hello", "hey", "hola", "greetings", "sup"
    );

    public void sendMessage(String channel, String text) throws IOException, SlackApiException {
        try {
            var slack = Slack.getInstance();
            var response = slack.methods(slackToken).chatPostMessage(req -> 
                req.channel(channel)
                   .text(text)
            );
            
            if (!response.isOk()) {
                log.error("Failed to send message: {}", response.getError());
                throw new RuntimeException("Failed to send message: " + response.getError());
            }
            
            log.info("Message sent successfully to channel: {}", channel);
        } catch (IOException | SlackApiException e) {
            log.error("Error sending message to Slack", e);
            throw e;
        }
    }

    public String handleMention(AppMentionEvent event) {
        try {
            String message = event.getText().toLowerCase();
            
            // Check if message contains any greeting
            boolean isGreeting = GREETINGS.stream()
                .anyMatch(greeting -> Pattern.compile("\\b" + greeting + "\\b")
                .matcher(message)
                .find());

            if (isGreeting) {
                String response = "Hey there! How's it going?";
                sendMessage(event.getChannel(), response);
            }
        } catch (IOException | SlackApiException e) {
            log.error("Error handling mention", e);
        }
        return "Hey there! How's it going?";
    }
}
