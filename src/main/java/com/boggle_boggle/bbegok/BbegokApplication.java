package com.boggle_boggle.bbegok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BbegokApplication {

	public static void main(String[] args) {
		SpringApplication.run(BbegokApplication.class, args);
	}

}
