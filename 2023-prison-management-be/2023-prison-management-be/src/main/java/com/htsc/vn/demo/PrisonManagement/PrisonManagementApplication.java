package com.htsc.vn.demo.PrisonManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Timer;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories
@EnableFeignClients
public class PrisonManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrisonManagementApplication.class, args);

	}

}
