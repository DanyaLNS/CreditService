package mts.fintech.creditservice.dto.output.successful.impl;

import lombok.Data;
import mts.fintech.creditservice.dto.output.entity.TariffDto;
import mts.fintech.creditservice.dto.output.successful.SuccessfulResponse;

import java.util.List;

@Data
public class AllTariffsDto implements SuccessfulResponse {
    private List<TariffDto> tariffs;
}
