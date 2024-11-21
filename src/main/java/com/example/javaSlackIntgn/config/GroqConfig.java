package com.example.javaSlackIntgn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

@Configuration
public class GroqConfig {

    private static final Logger logger = LoggerFactory.getLogger(GroqConfig.class);

    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Bean
    public WebClient groqWebClient() {
        logger.info("Initializing Groq WebClient");
        
        return WebClient.builder()
                .baseUrl(GROQ_API_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .filter((request, next) -> {
                    logger.debug("Making request to Groq API:");
                    logger.debug("URL: {}", request.url());
                    logger.debug("Method: {}", request.method());
                    logger.debug("Headers: {}", request.headers());
                    return next.exchange(request);
                })
                .build();
    }
}
