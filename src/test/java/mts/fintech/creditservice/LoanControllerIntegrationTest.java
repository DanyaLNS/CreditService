package mts.fintech.creditservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import mts.fintech.creditservice.controller.LoanController;
import mts.fintech.creditservice.dto.input.DeleteOrderDto;
import mts.fintech.creditservice.dto.input.LoanOrderInDto;
import mts.fintech.creditservice.entity.Tariff;
import mts.fintech.creditservice.exceptions.LoanOrderNotFoundException;
import mts.fintech.creditservice.exceptions.LoanOrderProcessingException;
import mts.fintech.creditservice.exceptions.OrderImpossibleToDeleteException;
import mts.fintech.creditservice.exceptions.TariffNotFoundException;
import mts.fintech.creditservice.service.LoanService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerIntegrationTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoanService loanService;

    private static List<Tariff> allTariffs;
    private static ObjectWriter ow;
    private static LoanOrderInDto orderToCreate;
    private static DeleteOrderDto orderToDelete;

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

        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        orderToCreate = new LoanOrderInDto();
        orderToCreate.setTariffId(1);
        orderToCreate.setUserId(1);

        orderToDelete = new DeleteOrderDto();
        orderToDelete.setOrderId("1");
        orderToDelete.setUserId(1);
    }

    @Test
    public void getAllTariffsTest() throws Exception {
        var expectedResponseContent = "{\"data\":{\"tariffs\":[{\"id\":1,\"type\":\"CONSUMER\",\"interest_rate\":\"14.5%\"},{\"id\":2,\"type\":\"MORTAGE\",\"interest_rate\":\"4.5%\"}]}}";

        when(loanService.getAllTariffs()).thenReturn(allTariffs);
        var response = mockMvc.perform(get("/loan-service/getTariffs"))
                .andExpect(status().isOk())
                .andReturn();

        var actualResponseContent = response.getResponse().getContentAsString();

        Assertions.assertEquals(expectedResponseContent, actualResponseContent);
        verify(loanService).getAllTariffs();
    }

    @Test
    public void getOrderStatusShouldReturnOkTest() throws Exception {
        var expectedResponseContent = "IN_PROGRESS";

        when(loanService.getOrderStatus("1")).thenReturn("IN_PROGRESS");

        mockMvc.perform(get("/loan-service/getStatusOrder")
                        .param("orderId", "1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.orderStatus").value(expectedResponseContent));
        verify(loanService).getOrderStatus("1");
    }

    @Test
    public void getOrderStatusShouldReturnBadRequestTest() throws Exception {
        var expectedResponseContent = "ORDER_NOT_FOUND";

        when(loanService.getOrderStatus("1")).thenThrow(new LoanOrderNotFoundException());

        mockMvc.perform(get("/loan-service/getStatusOrder")
                        .param("orderId", "1"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error.code").value(expectedResponseContent));

        verify(loanService).getOrderStatus("1");
    }

    @Test
    public void deleteOrderShouldReturnOkTest() throws Exception {

        String json = ow.writeValueAsString(orderToDelete);

        mockMvc.perform(delete("/loan-service/deleteOrder")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        verify(loanService).deleteLoanOrder(orderToDelete);
    }

    @Test
    public void deleteOrderShouldReturnRequestTest() throws Exception {
        var expectedResponseContent = "ORDER_IMPOSSIBLE_TO_DELETE";

        String json = ow.writeValueAsString(orderToDelete);

        Mockito.doThrow(new OrderImpossibleToDeleteException())
                .when(loanService)
                .deleteLoanOrder(Mockito.any());

        mockMvc.perform(delete("/loan-service/deleteOrder")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error.code").value(expectedResponseContent));

        verify(loanService).deleteLoanOrder(orderToDelete);
    }

    @Test
    public void createLoanOrderShouldReturnTariffNotFoundTest() throws Exception {
        var expectedResponseContent = "TARIFF_NOT_FOUND";

        String json = ow.writeValueAsString(orderToCreate);

        when(loanService.createLoanOrder(orderToCreate))
                .thenThrow(new TariffNotFoundException());


        mockMvc.perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error.code").value(expectedResponseContent));

        verify(loanService).createLoanOrder(orderToCreate);
    }

    @Test
    public void createLoanOrderShouldReturnLoanConsiderationTest() throws Exception {
        var expectedResponseContent = "LOAN_CONSIDERATION";

        String json = ow.writeValueAsString(orderToCreate);

        when(loanService.createLoanOrder(orderToCreate))
                .thenThrow(new LoanOrderProcessingException("LOAN_CONSIDERATION"));

        mockMvc.perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error.code").value(expectedResponseContent));

        verify(loanService).createLoanOrder(orderToCreate);
    }

    @Test
    public void createLoanOrderShouldReturnLoanAlreadyApprovedTest() throws Exception {
        var expectedResponseContent = "LOAN_ALREADY_APPROVED";

        String json = ow.writeValueAsString(orderToCreate);

        when(loanService.createLoanOrder(orderToCreate))
                .thenThrow(new LoanOrderProcessingException("LOAN_ALREADY_APPROVED"));

        mockMvc.perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error.code").value(expectedResponseContent));

        verify(loanService).createLoanOrder(orderToCreate);
    }

    @Test
    public void createLoanOrderShouldReturnTryLaterTest() throws Exception {
        var expectedResponseContent = "TRY_LATER";

        String json = ow.writeValueAsString(orderToCreate);

        when(loanService.createLoanOrder(orderToCreate))
                .thenThrow(new LoanOrderProcessingException("TRY_LATER"));

        mockMvc.perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error.code").value(expectedResponseContent));

        verify(loanService).createLoanOrder(orderToCreate);
    }

    @Test
    public void createLoanOrderShouldCreateNewOrderTest() throws Exception {
        var expectedResponseContent = "TRY_LATER";

        String json = ow.writeValueAsString(orderToCreate);

        when(loanService.createLoanOrder(orderToCreate))
                .thenReturn("qwerty");

        mockMvc.perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.orderId").value("qwerty"));

        verify(loanService).createLoanOrder(orderToCreate);
    }
}
