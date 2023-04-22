package mts.fintech.creditservice.dto.output.errors;

import lombok.Data;

@Data
public class ErrorMessage {
    private String code;
    private String message;
}
