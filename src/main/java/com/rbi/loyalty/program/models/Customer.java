package com.rbi.loyalty.program.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Customer {

    private Long customerId;
    private Integer pendingPoints;
    private Integer availablePoints;
    private LocalDate lastTransactionDate;
    private Integer consecutiveTransactionDays;

}
