package com.batchschedulerbasic;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class BatchSchedulerBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchSchedulerBasicApplication.class, args);
    }

}
