package com.autohub.audit;

import com.autohub.lib.annotation.AutoHubMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubMicroservice
public class AutoHubAuditApplication {

    static void main(String[] args) {
        SpringApplication.run(AutoHubAuditApplication.class, args);
    }

}
