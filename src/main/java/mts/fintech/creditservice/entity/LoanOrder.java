package mts.fintech.creditservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class LoanOrder {
    private long id;
    private String orderId;
    private long userId;
    private long tariffId;
    private double creditRating;
    private String status;
    private Timestamp timeInsert;
    private Timestamp timeUpdate;

    public LoanOrder(String orderId, long userId, long tariffId, double creditRating, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.tariffId = tariffId;
        this.creditRating = creditRating;
        this.status = status;
    }
}
