package com.ing.code.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.ing.code.model.Classification;
import com.ing.code.model.CustomerClassificationResponse;
import com.ing.code.model.CustomerInput;
import com.ing.code.model.CustomerTransaction;

@Component
@Service
public final class ClassificationServiceImpl {
	
	 public static ConcurrentMap<Integer, List<CustomerTransaction>> customerTransactionCache =
            new ConcurrentHashMap<>();
	 private static final String dataResource = "CustomerDetailsTransaction.json";

    private static final Type CUSTOMERTRANSACTIONTYPE = new TypeToken<List<CustomerTransaction>>() {
    }.getType();

    public void initializeCache() {
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create(); 
            JsonReader reader = null;
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(dataResource).getFile());
            reader = new JsonReader(new FileReader(file));
            List<CustomerTransaction> customerTransactionList = gson.fromJson(reader, CUSTOMERTRANSACTIONTYPE);
            customerTransactionCache =
                    customerTransactionList.stream()
                            .collect(Collectors.groupingByConcurrent(CustomerTransaction::getCustomerId));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public CustomerClassificationResponse getClassificationDetails(CustomerInput input) {
        CustomerClassificationResponse customerClassificationResponse=new CustomerClassificationResponse();
        List<CustomerTransaction> customerTransactionsList = customerTransactionCache.get(input.getCustomerId());
        if(customerTransactionsList == null || customerTransactionsList.isEmpty()) {
        	return customerClassificationResponse;
        }
        Set<Classification> classificationSet = new HashSet<>();
        Map<String, List<CustomerTransaction>> monthlyTransactionMap =
                getMonthlyTransactionMap(customerTransactionsList);
        String inputMonthYear =
                (Integer.toString(getMonthNumber(input.getSelectedMonth()) - 1)) + input.getSelectedYear();
        customerClassificationResponse.setMonthlyTramsactionList(monthlyTransactionMap.get(inputMonthYear));
        List<CustomerTransaction> txnListInMonthYear = monthlyTransactionMap.get(inputMonthYear);
        
        if (isCustomerAfterNoonPerson(input, txnListInMonthYear)) {
            classificationSet.add(Classification.AFTERNOON_PERSON);
        }
        if (isBigSpender(monthlyTransactionMap)) {
            classificationSet.add(Classification.BIG_SPENDER);
        }
        if (isBigTicketSpender(txnListInMonthYear)) {
            classificationSet.add(Classification.BIG_TICKET_SPENDER);
        }
        if (isFastSpender(customerTransactionsList)) {
            classificationSet.add(Classification.FAST_SPENDER);
        }
        if (isMorningPerson(input, txnListInMonthYear)) {
            classificationSet.add(Classification.MORNING_PERSON);
        }
        if (isPotentialSaver(monthlyTransactionMap)) {
            classificationSet.add(Classification.POTENTIAL_SAVER);
        }
        if (classificationSet.contains(Classification.BIG_SPENDER) &&
                classificationSet.contains(Classification.FAST_SPENDER)) {
            classificationSet.add(Classification.POTENTIAL_LOAN_CUSTOMER);
        }
        int currentBalance = calculateBalance(customerTransactionsList);
        customerClassificationResponse.setFinalBalance(currentBalance);
        customerClassificationResponse.setClassificationSet(classificationSet);
        return customerClassificationResponse;
    }

    private int calculateBalance(List<CustomerTransaction> customerTransactionsList) {
        int saving =
                customerTransactionsList.stream().filter(sav -> "credit".equalsIgnoreCase(sav.getTransactionType()))
                        .mapToInt(amount -> amount.getAmount()).sum();
        int spending =
                customerTransactionsList.stream().filter(sav -> "debit".equalsIgnoreCase(sav.getTransactionType()))
                        .mapToInt(amount -> amount.getAmount()).sum();
        return saving - spending;
    }

    private boolean isPotentialSaver(Map<String, List<CustomerTransaction>> monthlyTransactionMap) {
        for (Map.Entry<String, List<CustomerTransaction>> entry : monthlyTransactionMap.entrySet()) {
            List<CustomerTransaction> monthlyCustomerTransactionList = entry.getValue();
            double saving = 0;
            double spending = 0;
            for (CustomerTransaction eachCustomerTransaction : monthlyCustomerTransactionList) {
                if ("debit".equalsIgnoreCase(eachCustomerTransaction.getTransactionType())) {
                    spending += eachCustomerTransaction.getAmount();
                } else if ("credit".equalsIgnoreCase(eachCustomerTransaction.getTransactionType())) {
                    saving += eachCustomerTransaction.getAmount();
                }
            }

            if (calculatePercentage(spending, saving) > 25) {
                return false;
            }

        }
        return true;
    }


    private boolean isMorningPerson(CustomerInput input, List<CustomerTransaction> customerTransactionsList) {
        if (!customerTransactionsList.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            double countTotalMonthlyAfternoonTransaction = 0;
            for (CustomerTransaction eachCustomerTransaction : customerTransactionsList) {
                cal.setTime(eachCustomerTransaction.getDateTimeOfTransaction());
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                if (hour < 12) {
                    countTotalMonthlyAfternoonTransaction++;
                }
            }
            if (calculatePercentage(countTotalMonthlyAfternoonTransaction,
                    Double.valueOf(customerTransactionsList.size())) > 50) {
                return true;
            }
        }
        return false;
    }


    private boolean isFastSpender(List<CustomerTransaction> customerTransactionsList) {
        List<CustomerTransaction> savingList =
                customerTransactionsList.stream().filter(cus -> "credit".equalsIgnoreCase(cus.getTransactionType()))
                        .collect(
                                Collectors.toList());
        List<CustomerTransaction> spendingList =
                customerTransactionsList.stream().filter(cus -> "debit".equalsIgnoreCase(cus.getTransactionType()))
                        .collect(
                                Collectors.toList());
        Calendar cal = Calendar.getInstance();
        for (CustomerTransaction eachCustomerSavingTransaction : savingList) {
            cal.setTime(eachCustomerSavingTransaction.getDateTimeOfTransaction());
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date dateAddingSevenDays = cal.getTime();
            double totalspending = 0;
            for (CustomerTransaction eachCustomerSpendingTransaction : spendingList) {
                if (eachCustomerSavingTransaction.getDateTimeOfTransaction()
                        .before(eachCustomerSpendingTransaction.getDateTimeOfTransaction()) &&
                        eachCustomerSpendingTransaction.getDateTimeOfTransaction().before(dateAddingSevenDays)) {
                    totalspending += eachCustomerSpendingTransaction.getAmount();
                }
            }

            if (calculatePercentage(totalspending, Double.valueOf(eachCustomerSavingTransaction.getAmount())) > 75) {
                return true;
            }
        }

        return false;
    }

    private boolean isBigTicketSpender(List<CustomerTransaction> customerTransactions) {
        return customerTransactions.stream()
                .filter(cus -> ("debit".equalsIgnoreCase(cus.getTransactionType()) && cus.getAmount() > 1000))
                .count() >= 1;
    }

    private boolean isBigSpender(Map<String, List<CustomerTransaction>> monthlyTransactionMap) {
        for (Map.Entry<String, List<CustomerTransaction>> entry : monthlyTransactionMap.entrySet()) {
            List<CustomerTransaction> monthlyCustomerTransactionList = entry.getValue();
            double saving = 0;
            double spending = 0;
            for (CustomerTransaction eachCustomerTransaction : monthlyCustomerTransactionList) {
                if ("debit".equalsIgnoreCase(eachCustomerTransaction.getTransactionType())) {
                    spending += eachCustomerTransaction.getAmount();
                } else if ("credit".equalsIgnoreCase(eachCustomerTransaction.getTransactionType())) {
                    saving += eachCustomerTransaction.getAmount();
                }
            }

            if (calculatePercentage(spending, saving) < 80) {
                return false;
            }

        }
        return true;
    }

    private Map<String, List<CustomerTransaction>> getMonthlyTransactionMap(
            List<CustomerTransaction> customerTransactionsList) {
        Calendar cal = Calendar.getInstance();
        Map<String, List<CustomerTransaction>> monthlyTransactionMap = new HashMap<>();
        for (CustomerTransaction eachCustomerTransaction : customerTransactionsList) {
            cal.setTime(eachCustomerTransaction.getDateTimeOfTransaction());
            String year = Integer.toString(cal.get(Calendar.YEAR));
            String month = Integer.toString(cal.get(Calendar.MONTH));
            if (monthlyTransactionMap.containsKey(month + year)) {
                monthlyTransactionMap.get(month + year).add(eachCustomerTransaction);
            } else {
                List<CustomerTransaction> monthlyCustomerTransactionList = new ArrayList<>();
                monthlyCustomerTransactionList.add(eachCustomerTransaction);
                monthlyTransactionMap.put((month + year), monthlyCustomerTransactionList);
            }
        }
        return monthlyTransactionMap;
    }

    private boolean isCustomerAfterNoonPerson(CustomerInput input,
            List<CustomerTransaction> customerTransactionsList) {
        if (!customerTransactionsList.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            double countTotalMonthlyAfternoonTransaction = 0;
            for (CustomerTransaction eachCustomerTransaction : customerTransactionsList) {
                cal.setTime(eachCustomerTransaction.getDateTimeOfTransaction());
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                if (hour >= 12) {
                    countTotalMonthlyAfternoonTransaction++;
                }
            }
            if (calculatePercentage(countTotalMonthlyAfternoonTransaction,
                    Double.valueOf(customerTransactionsList.size())) > 50) {
                return true;
            }
        }
        return false;
    }

    private int getMonthNumber(String monthName) {
        return Month.valueOf(monthName.toUpperCase()).getValue();
    }

    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }
}