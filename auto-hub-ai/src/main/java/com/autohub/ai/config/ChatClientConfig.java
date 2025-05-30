package com.autohub.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder().build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.defaultSystem("""
                        You are a helpful assistant who can clearly and concisely answer questions about the type
                        of vehicle that is most suitable for traveling to a certain location in Romania in a certain
                        month of the year. You must provide one suggestion only.""")
                .build();
    }

}
