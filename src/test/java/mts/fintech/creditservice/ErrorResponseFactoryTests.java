package mts.fintech.creditservice;

import mts.fintech.creditservice.dto.output.errors.ErrorDto;
import mts.fintech.creditservice.enums.ErrorType;
import mts.fintech.creditservice.utils.ErrorResponseFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ErrorResponseFactoryTests {

    @Test
    public void getTariffNotFoundTest() {
        var expected = new ErrorDto("TARIFF_NOT_FOUND", "Тариф не найден");

        var actual = ErrorResponseFactory.getError(ErrorType.TARIFF_NOT_FOUND);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getStatusNotFoundTest() {
        var expected = new ErrorDto("ORDER_NOT_FOUND", "Заявка не найдена");

        var actual = ErrorResponseFactory.getError(ErrorType.ORDER_NOT_FOUND);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getDeleteErrorTest() {
        var expected = new ErrorDto("ORDER_IMPOSSIBLE_TO_DELETE", "Невозможно удалить заявку");

        var actual = ErrorResponseFactory.getError(ErrorType.DELETE_ERROR);

        Assertions.assertEquals(expected, actual);
    }
}
