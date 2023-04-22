package mts.fintech.creditservice.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LoanOrder {
    private long id;
    private String order_id;
    private long user_id;
    private long tariff_id;
    private double credit_rating;
    private String status;
    private Timestamp time_insert;
    private Timestamp time_update;
}
