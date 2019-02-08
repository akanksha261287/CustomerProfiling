package com.ing.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ing.code.service.ClassificationServiceImpl;

@SpringBootApplication
public class CustomerProfilingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerProfilingApplication.class, args);
		ClassificationServiceImpl classificationService=new ClassificationServiceImpl();
		classificationService.initializeCache();
	}

}

