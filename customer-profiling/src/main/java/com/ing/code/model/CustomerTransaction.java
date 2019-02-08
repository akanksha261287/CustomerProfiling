package com.ing.code.model;

import java.util.Date;

/**
 * TODO: Javadoc
 */
public final class CustomerTransaction {

    private Integer customerId;
    private Integer amount;
    private Date dateTimeOfTransaction;
    private String transactionType;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getDateTimeOfTransaction() {
        return dateTimeOfTransaction;
    }

    public void setDateTimeOfTransaction(Date dateTimeOfTransaction) {
        this.dateTimeOfTransaction = dateTimeOfTransaction;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


}