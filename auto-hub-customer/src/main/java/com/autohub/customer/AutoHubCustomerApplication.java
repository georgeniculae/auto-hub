package com.autohub.customer;

import com.autohub.lib.annotation.AutoHubMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubMicroservice
public class AutoHubCustomerApplication {

    static void main(String[] args) {
        SpringApplication.run(AutoHubCustomerApplication.class, args);
    }

}
