package mts.fintech.creditservice.dto.output.successful.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import mts.fintech.creditservice.dto.output.successful.SuccessfulResponse;

@Data
@AllArgsConstructor
public class LoanOrderDto extends SuccessfulResponse {
    private String orderId;
}
