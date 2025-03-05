package com.example.quizcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.quizcards.client")
public class QuizcardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizcardsApplication.class, args);
    }

}
