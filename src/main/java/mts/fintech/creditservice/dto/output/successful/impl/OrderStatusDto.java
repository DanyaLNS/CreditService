package mts.fintech.creditservice.dto.output.successful.impl;

import lombok.Data;
import mts.fintech.creditservice.dto.output.successful.SuccessfulResponse;

@Data
public class OrderStatusDto implements SuccessfulResponse {
    private String orderStatus;
}
