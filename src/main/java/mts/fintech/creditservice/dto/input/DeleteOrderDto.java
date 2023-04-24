package mts.fintech.creditservice.dto.input;

import lombok.Data;

@Data
public class DeleteOrderDto {
    private long userId;
    private String orderId;
}
