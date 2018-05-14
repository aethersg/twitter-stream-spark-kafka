package com.jude.spark.consumer.service;

import com.jude.spark.consumer.config.KafkaConsumerConfig;
import com.jude.spark.consumer.util.HashTagsUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.*;

@Service
public class SparkConsumerService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SparkConf sparkConf;
    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final Collection<String> topics;

    @Autowired
    public SparkConsumerService(SparkConf sparkConf,
                                KafkaConsumerConfig kafkaConsumerConfig,
                                @Value("${spring.kafka.template.default-topic}") String[] topics) {
        this.sparkConf = sparkConf;
        this.kafkaConsumerConfig = kafkaConsumerConfig;
        this.topics = Arrays.asList(topics);
    }

    public void run() {
        log.debug("Running Spark Consumer Service..");

        // Create context with a 10 seconds batch interval
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(10));

        // Create direct kafka stream with brokers and topics
        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaConsumerConfig.consumerConfigs()));

        // Get the lines, split them into words, count the words and print
        JavaDStream<String> lines = messages.map(ConsumerRecord::value);

        //Count the tweets and print
        lines
                .count()
                .map(cnt -> "Popular hash tags in last 60 seconds (" + cnt + " total tweets):")
                .print();

        //
        lines
                .flatMap(HashTagsUtils::hashTagsFromTweet)
                .mapToPair(hashTag -> new Tuple2<>(hashTag, 1))
                .reduceByKey(Integer::sum)

                .mapToPair(Tuple2::swap)
                .foreachRDD(rrdd -> {
                    System.out.println("---------------------------------------------------------------");
                    List<Tuple2<Integer, String>> sorted;
                    JavaPairRDD<Integer, String> counts = rrdd.sortByKey(false);
                    sorted = counts.collect();
                    sorted.forEach(record -> System.out.println(String.format(" %s (%d)", record._2, record._1)));
                });

        // Start the computation
        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
