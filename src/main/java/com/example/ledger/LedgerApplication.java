package com.example.ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class LedgerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LedgerApplication.class, args);
    }

    // Entry point when the WAR is deployed to an external Tomcat.
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(LedgerApplication.class);
    }
}
