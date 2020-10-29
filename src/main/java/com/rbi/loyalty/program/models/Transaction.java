package com.rbi.loyalty.program.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Transaction {

    private Long customerId;
    private Long transactionId;
    private Double transactionAmount;
    private LocalDate transactionDate;

}
