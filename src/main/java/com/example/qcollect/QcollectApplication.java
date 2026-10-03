package com.example.qcollect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class QcollectApplication {

	public static void main(String[] args) {
		SpringApplication.run(QcollectApplication.class, args);
	}

}
