package com.jude.spark.consumer;

import com.jude.spark.consumer.service.SparkConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Consumer implements CommandLineRunner {

    private final SparkConsumerService sparkConsumerService;

    @Autowired
    public Consumer(SparkConsumerService sparkConsumerService) {
        this.sparkConsumerService = sparkConsumerService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Consumer.class, args);
    }


    @Override
    public void run(String... strings) {
        sparkConsumerService.run();
    }
}