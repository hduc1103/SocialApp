package com.SocialWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@EnableJpaRepositories("com.SocialWeb.repository")
@EntityScan("com.SocialWeb.entity")
@EnableMongoRepositories(basePackages = "com.SocialWeb.mongorepository")
public class SocialAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocialAppApplication.class, args);
    }
}

