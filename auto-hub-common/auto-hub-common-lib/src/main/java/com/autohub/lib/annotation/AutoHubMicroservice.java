package com.autohub.lib.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication(excludeName = {
    "org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration",
    "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration"
})
@EnableJpaRepositories("com.autohub")
@ComponentScan(basePackages = {"com.autohub"})
@EntityScan("com.autohub")
@EnableDiscoveryClient
@EnableScheduling
@EnableRetry
@EnableConfigurationProperties
public @interface AutoHubMicroservice {
}
