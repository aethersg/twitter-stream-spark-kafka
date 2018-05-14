package com.jude.twitter.producer;

import com.jude.twitter.producer.service.StreamTweetEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Producer implements CommandLineRunner {

    private final
    StreamTweetEventService tweetEventService;

    @Autowired
    public Producer(StreamTweetEventService tweetEventService) {
        this.tweetEventService = tweetEventService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Producer.class, args);
    }

    @Override
    public void run(String... strings) {
        tweetEventService.run();
    }
}