package mts.fintech.creditservice.dto.output.successful;

import lombok.Data;

@Data
public class SuccessfulDto {
    private SuccessfulResponse data;

    public SuccessfulDto(SuccessfulResponse data) {
        this.data = data;
    }
}
