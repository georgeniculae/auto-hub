package com.autohub.agency;

import com.autohub.lib.annotation.AutoHubMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubMicroservice
public class AutoHubAgencyApplication {

    static void main(String[] args) {
        SpringApplication.run(AutoHubAgencyApplication.class, args);
    }

}
