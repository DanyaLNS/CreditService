package mts.fintech.creditservice.dto.output.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ErrorDto {
    private ErrorMessage error;
    public ErrorDto(String code, String message){
        error = new ErrorMessage();
        error.setCode(code);
        error.setMessage(message);
    }
}
