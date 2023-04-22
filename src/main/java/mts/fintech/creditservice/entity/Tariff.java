package mts.fintech.creditservice.entity;

import lombok.Data;

@Data
public class Tariff {
    private long id;
    private String type;
    private String interest_rate;
}
