package mts.fintech.creditservice.dto.output.successful.impl;

import lombok.Data;
import mts.fintech.creditservice.dto.output.successful.SuccessfulResponse;

@Data
public class LoanOrderDto implements SuccessfulResponse {
    private String orderId;
}
