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

    private final static String LOAN_CONSIDERATION_CODE = "LOAN_CONSIDERATION";
    private final static String  LOAN_CONSIDERATION_MESSAGE = "Заявка находится на рассмотрении";
    private final static String LOAN_ALREADY_APPROVED_CODE = "LOAN_ALREADY_APPROVED";
    private final static String LOAN_ALREADY_APPROVED_MESSAGE = "Заявка одобрена";
    private final static String TRY_LATER_CODE = "TRY_LATER";
    private final static String TRY_LATER_MESSAGE = "Заявка отклонена, попробуйте позже";

    public static ErrorDto getError(ErrorType errorType) {
        ErrorDto error;
        switch (errorType) {
            case TARIFF_NOT_FOUND -> error = new ErrorDto(TARIFF_NOT_FOUND_CODE, TARIFF_NOT_FOUND_MESSAGE);
            case ORDER_NOT_FOUND -> error = new ErrorDto(ORDER_NOT_FOUND_CODE, ORDER_NOT_FOUND_MESSAGE);
            case DELETE_ERROR -> error = new ErrorDto(DELETE_CODE, DELETE_MESSAGE);
            case LOAN_CONSIDERATION -> error = new ErrorDto(LOAN_CONSIDERATION_CODE, LOAN_CONSIDERATION_MESSAGE);
            case LOAN_ALREADY_APPROVED -> error = new ErrorDto(LOAN_ALREADY_APPROVED_CODE, LOAN_ALREADY_APPROVED_MESSAGE);
            case TRY_LATER -> error = new ErrorDto(TRY_LATER_CODE, TRY_LATER_MESSAGE);
            default -> error = new ErrorDto(UNKNOWN_CODE, UNKNOWN_MESSAGE);
        }
        return error;
    }
}
