package com.base.encode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EncodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EncodeApplication.class, args);
	}

}
