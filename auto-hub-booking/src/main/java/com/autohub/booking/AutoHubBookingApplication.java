package com.autohub.booking;

import com.autohub.lib.annotation.AutoHubMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubMicroservice
public class AutoHubBookingApplication {

    static void main(String[] args) {
        SpringApplication.run(AutoHubBookingApplication.class, args);
    }

}
