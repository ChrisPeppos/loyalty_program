package com.rbi.loyalty.program.services;

import com.rbi.loyalty.program.models.Customer;
import com.rbi.loyalty.program.models.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class LoyaltyProgramService {

    public SortedMap<Long, Customer> customerMap = new TreeMap<>();
    public SortedMap<Long, Transaction> transactionMap = new TreeMap<>();
    public SortedMap<LocalDate, Boolean> consecutiveDaysMap = new TreeMap<>();

    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.now().minusDays(7);

    public Boolean consecutiveDayCheck = false;

    public LoyaltyProgramService(SortedMap<LocalDate, Boolean> consecutiveDaysMap) {
        for (LocalDate date = startDate; date.isBefore(endDate) || date.isEqual(endDate); date = date.plusDays(1))
            consecutiveDaysMap.put(date, false);
        this.consecutiveDaysMap = consecutiveDaysMap;

    }

    public Customer getCustomer(Long customerId){
        return customerMap.get(customerId);
    }


    //Add new customer to List of Customers
    public void addNewCustomer(Long customerId){
        Customer newCustomer = new Customer();

        newCustomer.setCustomerId(customerId);
        newCustomer.setPendingPoints(0);
        newCustomer.setAvailablePoints(0);
        newCustomer.setConsecutiveTransactionDays(0);

        customerMap.put(newCustomer.getCustomerId(), newCustomer);

        System.out.println(newCustomer);
    }

    public boolean customerExists(Long customerId){
        return customerMap.containsKey(customerId);
    }

    //Create a new transaction for specific customer
    public void newTransaction(Transaction transaction, Long customerId){
        Double transactionAmount = Objects.nonNull(transaction.getTransactionAmount()) ? transaction.getTransactionAmount() : null;

        transactionMap.put(transaction.getTransactionId(), transaction);
        updateCustomerLoyaltyPoints(transaction, getCustomer(customerId));
        if(null == getCustomer(customerId).getLastTransactionDate() || getCustomer(customerId).getLastTransactionDate().isBefore(transaction.getTransactionDate())){
            getCustomer(customerId).setLastTransactionDate(transaction.getTransactionDate());
        }

        System.out.println(transaction);
    }

    //Update specific customer's amount spent and loyalty points
    public void updateCustomerLoyaltyPoints(Transaction transaction, Customer customer){
        Integer currentPoints = customer.getPendingPoints();
        Double amountSpent = transaction.getTransactionAmount();

        if(amountSpent > 7500){
                customer.setPendingPoints(currentPoints + 5000 + 5000 + (((int)Math.floor(amountSpent) - 7500) * 3));
        } else if(amountSpent > 5000) {
            customer.setPendingPoints(currentPoints + 5000 + (((int)Math.floor(amountSpent) - 5000) * 2));
        } else {
            customer.setPendingPoints(currentPoints + (int)Math.floor(amountSpent));
        }

    }

    public boolean checkPointsUpgradeStatus(Long customerId, Boolean eligibilityCheck){
        return upgradeStatus(customerId, eligibilityCheck);
    }

    public boolean upgradeStatus(Long customerId, Boolean eligibilityCheck){
        Double totalSpentOfWeek = 0.0;
        List<Transaction> transactionById = new ArrayList<>();

        for (Map.Entry<Long, Transaction> entry : transactionMap.entrySet()) {
            if (entry.getValue().getCustomerId().equals(customerId)) {
                transactionById.add(entry.getValue());
            }
        }
        if(getCustomer(customerId).getLastTransactionDate().plusDays(35).isBefore(endDate)){
            getCustomer(customerId).setPendingPoints(0);
            getCustomer(customerId).setAvailablePoints(0);
            System.out.println("User " + getCustomer(customerId).getCustomerId() + " has lost all points because no transaction was made in the last 5 weeks!");
        }
        if (eligibilityCheck == true && !(Objects.isNull(getCustomer(customerId).getLastTransactionDate()) || getCustomer(customerId).getLastTransactionDate().equals(LocalDate.now())))
            return false;

        for (LocalDate date = startDate; date.isBefore(endDate) || date.isEqual(endDate); date = date.plusDays(1)) {
            for (Transaction transaction : transactionById) {
                if (date.isEqual(transaction.getTransactionDate())) {
                    totalSpentOfWeek += transaction.getTransactionAmount();
                    if (Objects.nonNull(consecutiveDaysMap) && consecutiveDaysMap.get(transaction.getTransactionDate()) == false) {
                        getCustomer(customerId).setConsecutiveTransactionDays(getCustomer(customerId).getConsecutiveTransactionDays() + 1);
                        consecutiveDaysMap.put(date, true);
                    }
                }
            }
        }
        if (getCustomer(customerId).getConsecutiveTransactionDays() != 7 || totalSpentOfWeek < 500){
            return false;
        }
        return true;
    }
}
