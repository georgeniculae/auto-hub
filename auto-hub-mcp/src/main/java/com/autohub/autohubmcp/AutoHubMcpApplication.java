package com.autohub.autohubmcp;

import com.autohub.lib.annotation.AutoHubMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubMicroservice
public class AutoHubMcpApplication {

    static void main(String[] args) {
        SpringApplication.run(AutoHubMcpApplication.class, args);
    }

}
