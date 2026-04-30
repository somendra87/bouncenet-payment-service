package com.bouncenet.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BouncenetPaymentServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(BouncenetPaymentServiceApplication.class, args);
	}

}
