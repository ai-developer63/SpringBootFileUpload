package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.storage.StorageService;

@SpringBootApplication
public class UiVersionSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(UiVersionSpringBootApplication.class, args);
	}
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
//			storageService.deleteAll();
//			storageService.init();
		};
	}
}
