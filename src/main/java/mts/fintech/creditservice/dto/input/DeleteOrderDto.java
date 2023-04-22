package mts.fintech.creditservice.dto.input;

import lombok.Data;

@Data
public class DeleteOrderDto {
    private String userId;
    private String orderId;
}
