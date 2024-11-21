package com.example.javaSlackIntgn.model;

import java.util.List;

public class GroqResponse {
    private List<Choice> choices;

    // Getter
    public List<Choice> getChoices() {
        return choices;
    }

    // Inner class for Choice
    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }
    }

    // Inner class for Message
    public static class Message {
        private String content;

        public String getContent() {
            return content;
        }
    }
}
