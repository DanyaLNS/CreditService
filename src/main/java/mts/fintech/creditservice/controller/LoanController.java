package mts.fintech.creditservice.controller;

import mts.fintech.creditservice.dto.input.DeleteOrderDto;
import mts.fintech.creditservice.dto.input.LoanOrderInDto;
import mts.fintech.creditservice.dto.output.errors.ErrorDto;
import mts.fintech.creditservice.dto.output.successful.SuccessfulDto;
import mts.fintech.creditservice.dto.output.successful.impl.AllTariffsDto;
import mts.fintech.creditservice.dto.output.successful.impl.LoanOrderDto;
import mts.fintech.creditservice.dto.output.successful.impl.OrderStatusDto;
import mts.fintech.creditservice.enums.ErrorType;
import mts.fintech.creditservice.exceptions.LoanOrderNotFoundException;
import mts.fintech.creditservice.exceptions.LoanOrderProcessingException;
import mts.fintech.creditservice.exceptions.OrderImpossibleToDeleteException;
import mts.fintech.creditservice.exceptions.TariffNotFoundException;
import mts.fintech.creditservice.service.LoanService;
import mts.fintech.creditservice.utils.ErrorResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan-service")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/getTariffs")
    public ResponseEntity<SuccessfulDto> getAllTariffs() {
        return new ResponseEntity<>(
                new SuccessfulDto(
                        new AllTariffsDto(loanService.getAllTariffs())
                ), HttpStatus.OK
        );
    }

    @PostMapping("/order")
    public ResponseEntity<SuccessfulDto> createLoanOrder(@RequestBody LoanOrderInDto order) throws TariffNotFoundException, LoanOrderProcessingException {
        return new ResponseEntity<>(
                new SuccessfulDto(
                        new LoanOrderDto(loanService.createLoanOrder(order))),
                HttpStatus.OK);
    }

    @GetMapping("/getStatusOrder")
    public ResponseEntity<SuccessfulDto> getStatusOrder(@RequestParam String orderId) throws LoanOrderNotFoundException {
        return new ResponseEntity<>(
                new SuccessfulDto(
                        new OrderStatusDto(loanService.getOrderStatus(orderId))),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/deleteOrder")
    public ResponseEntity<Void> deleteOrder(@RequestBody DeleteOrderDto deleteOrderDto) throws LoanOrderNotFoundException, OrderImpossibleToDeleteException {
        loanService.deleteLoanOrder(deleteOrderDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        switch (ex.getMessage()) {
            case "TARIFF_NOT_FOUND" -> {
                return getError(ErrorType.TARIFF_NOT_FOUND);
            }
            case "LOAN_CONSIDERATION" -> {
                return getError(ErrorType.LOAN_CONSIDERATION);
            }
            case "LOAN_ALREADY_APPROVED" -> {
                return getError(ErrorType.LOAN_ALREADY_APPROVED);
            }
            case "TRY_LATER" -> {
                return getError(ErrorType.TRY_LATER);
            }
            case "ORDER_NOT_FOUND" -> {
                return getError(ErrorType.ORDER_NOT_FOUND);
            }
            case "IMPOSSIBLE_TO_DELETE" -> {
                return getError(ErrorType.DELETE_ERROR);
            }
            default -> {
                return getError(ErrorType.UNKNOWN_ERROR);
            }
        }
    }

    private ResponseEntity getError(ErrorType type) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseFactory.getError(type));
    }
}
