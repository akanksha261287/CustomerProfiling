package com.ing.code.model;

import java.util.List;
import java.util.Set;

/**
 * TODO: Javadoc
 */
public final class CustomerClassificationResponse {

    private Set<Classification> classificationSet;
    private List<CustomerTransaction> monthlyTramsactionList;
    private int finalBalance;

    public Set<Classification> getClassificationSet() {
        return classificationSet;
    }

    public void setClassificationSet(Set<Classification> classificationSet) {
        this.classificationSet = classificationSet;
    }

    public List<CustomerTransaction> getMonthlyTramsactionList() {
        return monthlyTramsactionList;
    }

    public void setMonthlyTramsactionList(List<CustomerTransaction> monthlyTramsactionList) {
        this.monthlyTramsactionList = monthlyTramsactionList;
    }

    public int getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(int finalBalance) {
        this.finalBalance = finalBalance;
    }

}