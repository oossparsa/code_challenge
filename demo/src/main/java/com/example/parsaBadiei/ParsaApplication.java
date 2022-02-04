package com.example.parsaBadiei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileUploadProperties.class})
public class ParsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParsaApplication.class, args);
	}

}
