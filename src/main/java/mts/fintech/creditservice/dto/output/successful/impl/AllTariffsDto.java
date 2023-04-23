package mts.fintech.creditservice.dto.output.successful.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import mts.fintech.creditservice.dto.output.successful.SuccessfulResponse;
import mts.fintech.creditservice.entity.Tariff;

import java.util.List;

@Data
@AllArgsConstructor
public class AllTariffsDto implements SuccessfulResponse {
    private List<Tariff> tariffs;
}
