package com.example.javaSlackIntgn.service;

import com.example.javaSlackIntgn.model.GroqRequest;
import com.example.javaSlackIntgn.model.GroqResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {
    private final WebClient groqWebClient;
    private final Logger logger = LoggerFactory.getLogger(AIService.class);
    
    @Value("${slack.bot.token}")
    private String slackToken;

    public AIService(WebClient groqWebClient) {
        this.groqWebClient = groqWebClient;
    }

    private void sendSlackMessage(String channel, String text) {
        try {
            var slack = Slack.getInstance();
            var response = slack.methods(slackToken).chatPostMessage(req -> 
                req.channel(channel)
                   .text(text)
            );
            
            if (!response.isOk()) {
                logger.error("Failed to send message: {}", response.getError());
                throw new RuntimeException("Failed to send message: " + response.getError());
            }
            
            logger.info("Message sent successfully to channel: {}", channel);
        } catch (IOException | SlackApiException e) {
            logger.error("Error sending message to Slack", e);
            throw new RuntimeException("Failed to send message to Slack", e);
        }
    }

    public String generateResponse(String userMessage, String channel) {
        List<GroqRequest.Message> messages = new ArrayList<>();

        messages.add(new GroqRequest.Message("system",
                "You are a helpful assistant. Keep responses under 2000 characters."));
        messages.add(new GroqRequest.Message("user", userMessage));

        GroqRequest request = new GroqRequest(
                "mixtral-8x7b-32768", // Updated to a valid Groq model
                messages,
                0.7,
                2000);

        logger.debug("Full request body: {}", request); // More detailed logging

        return groqWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GroqResponse.class)
                .map(GroqResponse::getChoices)
                .map(choices -> {
                    if (choices == null || choices.isEmpty()) {
                        throw new RuntimeException("No response from Groq API");
                    }
                    return choices.get(0).getMessage().getContent();
                })
                .doOnSuccess(response -> {
                    logger.info("Successfully received response from Groq");
                    sendSlackMessage(channel, response);
                })
                .doOnError(e -> logger.error("Error in Groq API call: ", e))
                .onErrorResume(e -> Mono.just("I apologize, but I'm having trouble generating a response right now."))
                .block();
    }
}
