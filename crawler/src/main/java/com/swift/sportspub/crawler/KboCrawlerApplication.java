package com.swift.sportspub.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KboCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KboCrawlerApplication.class, args);
    }
}
