package com.ing.code;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ing.code.model.Classification;
import com.ing.code.model.CustomerClassificationResponse;
import com.ing.code.model.CustomerInput;
import com.ing.code.service.ClassificationServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerProfilingApplicationTests {

    @Autowired
    ClassificationServiceImpl service;

    private static boolean isSetupDone=false;

    @Before
    public  void setup() {
        if(!isSetupDone) {
            service.initializeCache();
            isSetupDone=true;
        }
    }


    @Test
    public void shouldReturnAfterNoonPersonAsClassificationType() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(1234);
        input.setSelectedMonth("January");
        input.setSelectedYear("2019");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Classification expected is afternoon person",
                customerClassificationResponse.getClassificationSet().contains(Classification.AFTERNOON_PERSON));

    }

    @Test
    public void shouldReturnBigSpenderAsClassificationType() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(5678);
        input.setSelectedMonth("February");
        input.setSelectedYear("2018");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Classification expected is big spender person",
                customerClassificationResponse.getClassificationSet().contains(Classification.BIG_SPENDER));

    }

    @Test
    public void shouldReturnBigTicketSpenderAsClassificationType() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(5678);
        input.setSelectedMonth("February");
        input.setSelectedYear("2018");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Classification expected is big ticket spender person",
                customerClassificationResponse.getClassificationSet().contains(Classification.BIG_TICKET_SPENDER));

    }

    @Test
    public void shouldReturnFastSpenderAsClassificationType() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(7777);
        input.setSelectedMonth("April");
        input.setSelectedYear("2018");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Classification expected is fast spender person",
                customerClassificationResponse.getClassificationSet().contains(Classification.FAST_SPENDER));

    }

    @Test
    public void shouldReturnMorningPersonAsClassificationType() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(7777);
        input.setSelectedMonth("April");
        input.setSelectedYear("2018");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Classification expected is morning person",
                customerClassificationResponse.getClassificationSet().contains(Classification.MORNING_PERSON));

    }

    @Test
    public void shouldReturnPotentialLoanCustomerAsClassificationType() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(7777);
        input.setSelectedMonth("April");
        input.setSelectedYear("2018");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Classification expected is potential loan customer",
                customerClassificationResponse.getClassificationSet().contains(Classification.POTENTIAL_LOAN_CUSTOMER));
    }

    @Test
    public void shouldReturnPotentialSaverAsClassificationType() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(6123);
        input.setSelectedMonth("October");
        input.setSelectedYear("2018");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Classification expected is potential saver",
                customerClassificationResponse.getClassificationSet().contains(Classification.POTENTIAL_SAVER));
    }

    @Test
    public void validateBalance() {
        CustomerInput input = new CustomerInput();
        input.setCustomerId(7777);
        input.setSelectedMonth("April");
        input.setSelectedYear("2018");
        CustomerClassificationResponse customerClassificationResponse = service.getClassificationDetails(input);
        assertTrue("Balance didn't match",
                customerClassificationResponse.getFinalBalance()==300);
    }
}

