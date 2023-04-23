package mts.fintech.creditservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class LoanOrder {
    private long id;
    private String order_id;
    private long user_id;
    private long tariff_id;
    private double credit_rating;
    private String status;
    private Timestamp time_insert;
    private Timestamp time_update;

    public LoanOrder(String order_id, long user_id, long tariff_id, double credit_rating, String status) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.tariff_id = tariff_id;
        this.credit_rating = credit_rating;
        this.status = status;
    }
}
