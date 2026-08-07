package com.autohub.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class AiRestClientConfig {

    /**
     * Ollama is reached at a fixed host and port, not through service discovery, but Spring AI's
     * {@code OllamaApiAutoConfiguration} builds its client from whichever {@code RestClient.Builder}
     * the context happens to offer. Without this bean the only candidate is the {@code @LoadBalanced}
     * builder from auto-hub-common-lib, so the load balancer reads the {@code localhost} host as a
     * service id and every call fails with "Service Instance cannot be null, serviceId: localhost".
     * <p>
     * Beans that do want service discovery ask for {@code loadBalancedRestClientBuilder} by name, so
     * making the plain builder primary only affects clients that take whatever they are given.
     */
    @Bean
    @Primary
    public RestClient.Builder plainRestClientBuilder() {
        return RestClient.builder();
    }

}
