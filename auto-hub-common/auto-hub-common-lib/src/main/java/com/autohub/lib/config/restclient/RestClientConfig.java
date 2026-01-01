package com.autohub.lib.config.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Configuration
public class RestClientConfig {

    @Bean(name = "loadBalancedRestClientBuilder")
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = ClientHttpRequestFactoryBuilder.httpComponents()
                .withCustomizers(
                        List.of(
                                requestFactory -> requestFactory.setConnectionRequestTimeout(Duration.ofSeconds(60)),
                                requestFactory -> requestFactory.setReadTimeout(Duration.ofSeconds(60))
                        )
                )
                .build();

        return RestClient.builder().requestFactory(clientHttpRequestFactory);
    }

    @Bean
    public RestClient restClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }

}
