package com.rishtabox.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {

		System.setProperty("java.net.preferIPv6Addresses", "true");
		System.setProperty("java.net.preferIPv4Stack", "false");

		SpringApplication.run(BackendApplication.class, args);
	}
}