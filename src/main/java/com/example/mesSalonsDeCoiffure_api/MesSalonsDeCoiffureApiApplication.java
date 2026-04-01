package com.example.mesSalonsDeCoiffure_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MesSalonsDeCoiffureApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MesSalonsDeCoiffureApiApplication.class, args);
	}

}
