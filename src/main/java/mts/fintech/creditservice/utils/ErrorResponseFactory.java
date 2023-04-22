package mts.fintech.creditservice.utils;

import mts.fintech.creditservice.dto.output.errors.ErrorDto;
import mts.fintech.creditservice.enums.ErrorType;

public class ErrorResponseFactory {
    private final static String TARIFF_NOT_FOUND_CODE = "TARIFF_NOT_FOUND";
    private final static String TARIFF_NOT_FOUND_MESSAGE = "Тариф не найден";

    private final static String ORDER_NOT_FOUND_CODE = "ORDER_NOT_FOUND";
    private final static String ORDER_NOT_FOUND_MESSAGE = "Заявка не найдена";
    private final static String DELETE_CODE = "ORDER_IMPOSSIBLE_TO_DELETE";
    private final static String DELETE_MESSAGE = "Невозможно удалить заявку";
    private final static String UNKNOWN_CODE = "UNKNOWN_ERROR_TYPE";
    private final static String UNKNOWN_MESSAGE = "Неизвестный тип ошибки";


    public static ErrorDto getError(ErrorType errorType) {
        ErrorDto error;
        switch (errorType) {
            case TARIFF_NOT_FOUND -> error = new ErrorDto(TARIFF_NOT_FOUND_CODE, TARIFF_NOT_FOUND_MESSAGE);
            case ORDER_NOT_FOUND -> error = new ErrorDto(ORDER_NOT_FOUND_CODE, ORDER_NOT_FOUND_MESSAGE);
            case DELETE_ERROR -> error = new ErrorDto(DELETE_CODE, DELETE_MESSAGE);
            default -> error = new ErrorDto(UNKNOWN_CODE, UNKNOWN_MESSAGE);
        }
        return error;
    }
}
