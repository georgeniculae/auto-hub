package com.autohub.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AutoHubApiGatewayApplication {

    static void main(String[] args) {
        SpringApplication.run(AutoHubApiGatewayApplication.class, args);
    }

}
