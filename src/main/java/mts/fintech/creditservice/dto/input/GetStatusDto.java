package mts.fintech.creditservice.dto.input;

import lombok.Data;

@Data
public class GetStatusDto {
    private long userId;
    private long tariffId;
}
