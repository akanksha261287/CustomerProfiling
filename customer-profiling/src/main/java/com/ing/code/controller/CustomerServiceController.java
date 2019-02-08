package com.ing.code.controller;

import com.ing.code.model.CustomerClassificationResponse;
import com.ing.code.service.ClassificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ing.code.model.CustomerInput;

;

@RestController
@RequestMapping("/api")
public class CustomerServiceController {

	@Autowired
	ClassificationServiceImpl service;

	@RequestMapping(value="/GetCustomers" , method = RequestMethod.POST)
	public CustomerClassificationResponse getCustomers(@RequestBody  CustomerInput input) {
	   CustomerClassificationResponse customerClassificationResponse=service.getClassificationDetails(input);
	   return customerClassificationResponse;
	}

}
