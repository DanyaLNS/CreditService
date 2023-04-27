package mts.fintech.creditservice;

import mts.fintech.creditservice.dto.input.DeleteOrderDto;
import mts.fintech.creditservice.dto.input.LoanOrderInDto;
import mts.fintech.creditservice.entity.LoanOrder;
import mts.fintech.creditservice.entity.Tariff;
import mts.fintech.creditservice.exceptions.LoanOrderNotFoundException;
import mts.fintech.creditservice.exceptions.LoanOrderProcessingException;
import mts.fintech.creditservice.exceptions.OrderImpossibleToDeleteException;
import mts.fintech.creditservice.exceptions.TariffNotFoundException;
import mts.fintech.creditservice.repository.impl.LoanOrderRepositoryImpl;
import mts.fintech.creditservice.repository.impl.TariffRepositoryImpl;
import mts.fintech.creditservice.service.LoanService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class LoanServiceUnitTests {
    @MockBean
    private TariffRepositoryImpl tariffRepository;
    @MockBean
    private LoanOrderRepositoryImpl loanOrderRepository;
    @Autowired
    private LoanService loanService;
    private static List<Tariff> allTariffs;
    private static LoanOrder order;
    private static DeleteOrderDto orderToDelete;
    private static LoanOrderInDto orderToCreate;

    private final static String ORDER_ID = "qwerty";
    private final static String IN_PROGRESS_STATUS = "IN_PROGRESS";


    @BeforeAll
    public static void init() {
        var tariff1 = new Tariff();
        tariff1.setId(1);
        tariff1.setType("CONSUMER");
        tariff1.setInterest_rate("14.5%");
        var tariff2 = new Tariff();
        tariff2.setId(2);
        tariff2.setType("MORTAGE");
        tariff2.setInterest_rate("4.5%");
        allTariffs = List.of(tariff1, tariff2);

        order = new LoanOrder(
                ORDER_ID,
                1,
                1,
                0.56,
                IN_PROGRESS_STATUS
        );


        orderToCreate = new LoanOrderInDto();
        orderToCreate.setTariffId(1);
        orderToCreate.setUserId(1);

        orderToDelete = new DeleteOrderDto();
        orderToDelete.setOrderId("1");
        orderToDelete.setUserId(1);
    }

    @Test
    public void getAllTariffsTest() {
        when(tariffRepository.findAll()).thenReturn(allTariffs);

        var actual = loanService.getAllTariffs();

        Assertions.assertIterableEquals(allTariffs, actual);
        verify(tariffRepository).findAll();
    }

    @Test
    public void getOrderStatusShouldReturnInProgressTest() throws LoanOrderNotFoundException {
        order.setStatus(IN_PROGRESS_STATUS);
        when(loanOrderRepository.findByOrderId(ORDER_ID))
                .thenReturn(order);

        var actual = loanService.getOrderStatus(ORDER_ID);

        Assertions.assertEquals(IN_PROGRESS_STATUS, actual);
        verify(loanOrderRepository).findByOrderId(ORDER_ID);
    }

    @Test
    public void getOrderStatusShouldThrowNotFoundExceptionTest() throws LoanOrderNotFoundException {
        when(loanOrderRepository.findByOrderId(ORDER_ID))
                .thenThrow(new LoanOrderNotFoundException());

        Assertions.assertThrows(LoanOrderNotFoundException.class,
                () -> loanService.getOrderStatus(ORDER_ID));
        verify(loanOrderRepository).findByOrderId(ORDER_ID);
    }

    @Test
    public void deleteOrderShouldDeleteTest() throws LoanOrderNotFoundException, OrderImpossibleToDeleteException {
        order.setStatus(IN_PROGRESS_STATUS);
        when(loanOrderRepository.findByUserIdAndOrderId(orderToDelete.getUserId(), orderToDelete.getOrderId()))
                .thenReturn(order);

        loanService.deleteLoanOrder(orderToDelete);
        verify(loanOrderRepository).findByUserIdAndOrderId(orderToDelete.getUserId(), orderToDelete.getOrderId());
        verify(loanOrderRepository).deleteById(order.getId());
    }

    @Test
    public void deleteOrderShouldThrowOrderNotFoundExceptionTest() throws LoanOrderNotFoundException {
        when(loanOrderRepository.findByUserIdAndOrderId(orderToDelete.getUserId(), orderToDelete.getOrderId()))
                .thenThrow(new LoanOrderNotFoundException());

        Assertions.assertThrows(LoanOrderNotFoundException.class,
                () -> loanService.deleteLoanOrder(orderToDelete));
        verify(loanOrderRepository).findByUserIdAndOrderId(orderToDelete.getUserId(), orderToDelete.getOrderId());
    }

    @Test
    public void deleteOrderShouldThrowOrderImpossibleToDeleteException() throws LoanOrderNotFoundException, OrderImpossibleToDeleteException {
        order.setStatus("REFUSED");

        when(loanOrderRepository.findByUserIdAndOrderId(orderToDelete.getUserId(), orderToDelete.getOrderId()))
                .thenReturn(order);

        Assertions.assertThrows(OrderImpossibleToDeleteException.class,
                () -> loanService.deleteLoanOrder(orderToDelete));
        verify(loanOrderRepository).findByUserIdAndOrderId(orderToDelete.getUserId(), orderToDelete.getOrderId());
    }

    @Test
    public void applyingForALoanShouldThrowTariffNotFoundTest() throws TariffNotFoundException {
        when(tariffRepository.findById(orderToCreate.getTariffId()))
                .thenThrow(new TariffNotFoundException());

        Assertions.assertThrows(TariffNotFoundException.class,
                () -> loanService.createLoanOrder(orderToCreate));
        verify(tariffRepository).findById(orderToCreate.getTariffId());
    }

    @Test
    public void applyingForALoanShouldCreateNewOrderTest() throws LoanOrderNotFoundException, TariffNotFoundException, LoanOrderProcessingException {
        when(loanOrderRepository.findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId()))
                .thenThrow(new LoanOrderNotFoundException());
        var result = loanService.createLoanOrder(orderToCreate);

        Assertions.assertDoesNotThrow(() -> loanService.createLoanOrder(orderToCreate));
        Assertions.assertTrue(result instanceof String);
        Assertions.assertTrue(!result.equals(null));

        verify(loanOrderRepository, times(2)).findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId());
        verify(tariffRepository, times(2)).findById(orderToCreate.getTariffId());
    }

    @Test
    public void applyingForALoanShouldCreateNewOrderBecausePreviousWasRefusedTest() throws LoanOrderNotFoundException, TariffNotFoundException, LoanOrderProcessingException {
        order.setStatus("REFUSED");
        Timestamp insertDate = Timestamp.valueOf("2020-03-12 12:10:09.968");
        Timestamp updateDate = Timestamp.valueOf("2021-06-10 13:32:15.768");
        order.setTimeInsert(insertDate);
        order.setTimeUpdate(updateDate);

        when(loanOrderRepository.findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId()))
                .thenReturn(order);

        var result = loanService.createLoanOrder(orderToCreate);
        Assertions.assertDoesNotThrow(() -> loanService.createLoanOrder(orderToCreate));
        Assertions.assertTrue(result instanceof String);
        Assertions.assertTrue(!result.equals(null));

        verify(loanOrderRepository, times(2)).findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId());
        verify(tariffRepository, times(2)).findById(orderToCreate.getTariffId());
    }

    @Test
    public void applyingForALoanShouldThrowProcessingExceptionWithConsiderationTest() throws LoanOrderNotFoundException, TariffNotFoundException, LoanOrderProcessingException {
        when(loanOrderRepository.findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId()))
                .thenReturn(order);

        Assertions.assertThrows(LoanOrderProcessingException.class,
                () -> loanService.createLoanOrder(orderToCreate));

        verify(loanOrderRepository).findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId());
        verify(tariffRepository).findById(orderToCreate.getTariffId());
    }

    @Test
    public void applyingForALoanShouldThrowProcessingExceptionWithAlreadyApprovedTest() throws LoanOrderNotFoundException, TariffNotFoundException {
        order.setStatus("APPROVED");
        when(loanOrderRepository.findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId()))
                .thenReturn(order);

        Assertions.assertThrows(LoanOrderProcessingException.class,
                () -> loanService.createLoanOrder(orderToCreate));

        verify(loanOrderRepository).findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId());
        verify(tariffRepository).findById(orderToCreate.getTariffId());
    }

    @Test
    public void applyingForALoanShouldThrowProcessingExceptionWithTryLaterTest() throws LoanOrderNotFoundException, TariffNotFoundException {
        order.setStatus("REFUSED");
        order.setTimeUpdate(new Timestamp(System.currentTimeMillis()));
        when(loanOrderRepository.findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId()))
                .thenReturn(order);

        Assertions.assertThrows(LoanOrderProcessingException.class,
                () -> loanService.createLoanOrder(orderToCreate));

        verify(loanOrderRepository).findByUserIdAndTariffId(orderToCreate.getUserId(), orderToCreate.getTariffId());
        verify(tariffRepository).findById(orderToCreate.getTariffId());
    }
}
