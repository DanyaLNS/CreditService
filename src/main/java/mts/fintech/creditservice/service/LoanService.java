package mts.fintech.creditservice.service;

import mts.fintech.creditservice.dto.input.DeleteOrderDto;
import mts.fintech.creditservice.dto.input.LoanOrderInDto;
import mts.fintech.creditservice.entity.LoanOrder;
import mts.fintech.creditservice.entity.Tariff;
import mts.fintech.creditservice.exceptions.*;
import mts.fintech.creditservice.repository.impl.LoanOrderRepositoryImpl;
import mts.fintech.creditservice.repository.impl.TariffRepositoryImpl;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LoanService {
    private final TariffRepositoryImpl tariffRepository;
    private final LoanOrderRepositoryImpl orderRepository;

    private final int TWO_MINUTES = 120_000;

    public LoanService(TariffRepositoryImpl tariffRepository, LoanOrderRepositoryImpl orderRepository) {
        this.tariffRepository = tariffRepository;
        this.orderRepository = orderRepository;
    }

    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    public String createLoanOrder(LoanOrderInDto order) throws TariffNotFoundException, LoanOrderProcessingException {
        try {
            tariffRepository.findById(order.getTariffId());
            LoanOrder loanOrder = orderRepository.findByUserIdAndTariffId(order.getUserId(), order.getTariffId());
            return processLoanOrder(order, loanOrder);
        } catch (TariffNotFoundException ex) {
            throw ex;
        } catch (LoanOrderNotFoundException ex) {
            return saveOrder(order);
        }
    }

    private String processLoanOrder(LoanOrderInDto order, LoanOrder loanOrder) throws LoanOrderProcessingException {
        switch (loanOrder.getStatus()) {
            case "IN_PROGRESS" -> {
                throw new LoanOrderProcessingException("LOAN_CONSIDERATION");
            }
            case "APPROVED" -> {
                throw new LoanOrderProcessingException("LOAN_ALREADY_APPROVED");
            }
            case "REFUSED" -> {
                if (dateDiffWithCurrentTime(loanOrder.getTime_update()) < TWO_MINUTES) {
                    throw new LoanOrderProcessingException("TRY_LATER");
                } else {
                    return saveOrder(order);
                }
            }
            default -> {
                return saveOrder(order);
            }
        }
    }

    private long dateDiffWithCurrentTime(Timestamp time_update) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return currentTime.getTime() - time_update.getTime();
    }

    private String saveOrder(LoanOrderInDto order) {
        LoanOrder inputOrder = generateOrder(order.getUserId(), order.getTariffId());
        orderRepository.save(inputOrder);
        return inputOrder.getOrder_id();
    }

    private LoanOrder generateOrder(long user_id, long tariff_id) {
        String orderId = UUID.randomUUID().toString();
        double credit_rating = generateRating();
        LoanOrder order = new LoanOrder(
                orderId,
                user_id,
                tariff_id,
                credit_rating,
                "IN_PROGRESS");
        return order;
    }

    public static double generateRating() {
        double rating = ThreadLocalRandom.current().nextDouble(0.1, 0.9);
        double formalized_rating = Double.valueOf(
                String.format("%." + 2 + "f", rating)
                        .replace(",", "."));
        return formalized_rating;
    }

    public String getOrderStatus(String orderId) throws LoanOrderNotFoundException {
        try {
            return orderRepository
                    .findByOrderId(orderId)
                    .getStatus();
        } catch (LoanOrderNotFoundException ex) {
            throw ex;
        }
    }

    public void deleteLoanOrder(DeleteOrderDto deleteOrderDto) throws LoanOrderNotFoundException, OrderImpossibleToDeleteException {
        LoanOrder loanOrder = orderRepository.findByUserIdAndOrderId(deleteOrderDto.getUserId(), deleteOrderDto.getOrderId());
        System.out.println(loanOrder.getStatus());
        if(loanOrder.getStatus().equals("IN_PROGRESS")) {
            orderRepository.deleteById(loanOrder.getId());
        } else {
            throw new OrderImpossibleToDeleteException();
        }
    }
}
