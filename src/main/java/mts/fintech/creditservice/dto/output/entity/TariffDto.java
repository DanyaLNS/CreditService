package mts.fintech.creditservice.dto.output.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TariffDto {
    private long id;
    private String type;
    private String interest_rate;
}
