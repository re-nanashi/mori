package com.mori.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MoriEurekaServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoriEurekaServerApplication.class, args);
	}
}