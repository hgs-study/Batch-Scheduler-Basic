package com.batchschedulerbasic;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableBatchProcessing
@SpringBootApplication
@EnableJpaAuditing
public class BatchSchedulerBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchSchedulerBasicApplication.class, args);
    }
}

