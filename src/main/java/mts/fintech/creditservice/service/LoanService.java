package mts.fintech.creditservice.service;

import lombok.extern.log4j.Log4j2;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@EnableScheduling
@Log4j2
public class LoanService {
    private final TariffRepositoryImpl tariffRepository;
    private final LoanOrderRepositoryImpl orderRepository;
    private final static String APPROVED_STATUS = "APPROVED";
    private final static String IN_PROGRESS_STATUS = "IN_PROGRESS";
    private final static String REFUSED_STATUS = "REFUSED";

    private final int TWO_MINUTES = 120_000;

    public LoanService(TariffRepositoryImpl tariffRepository, LoanOrderRepositoryImpl orderRepository) {
        this.tariffRepository = tariffRepository;
        this.orderRepository = orderRepository;
    }

    public List<Tariff> getAllTariffs() {
        log.info("Get all tariffs");
        return tariffRepository.findAll();
    }

    public String createLoanOrder(LoanOrderInDto order) throws TariffNotFoundException, LoanOrderProcessingException {
        try {
            tariffRepository.findById(order.getTariffId());
            log.info("Tariff successfully found");
            LoanOrder loanOrder = orderRepository.findByUserIdAndTariffId(order.getUserId(), order.getTariffId());
            log.info("Loan order successfully found: processing order");
            return processLoanOrder(order, loanOrder);
        } catch (TariffNotFoundException ex) {
            log.error("Tariff not found");
            throw ex;
        } catch (LoanOrderNotFoundException ex) {
            log.error("Loan order not found: creating new");
            return saveOrder(order);
        }
    }

    private String processLoanOrder(LoanOrderInDto order, LoanOrder loanOrder) throws LoanOrderProcessingException {
        switch (loanOrder.getStatus()) {
            case IN_PROGRESS_STATUS -> {
                throw new LoanOrderProcessingException("LOAN_CONSIDERATION");
            }
            case APPROVED_STATUS -> {
                throw new LoanOrderProcessingException("LOAN_ALREADY_APPROVED");
            }
            case REFUSED_STATUS -> {
                if (dateDiffWithCurrentTime(loanOrder.getTimeUpdate()) < TWO_MINUTES) {
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
        log.info("Calculate date diff");
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return currentTime.getTime() - time_update.getTime();
    }

    private String saveOrder(LoanOrderInDto order) {
        log.info("Saving loan order");
        LoanOrder inputOrder = generateOrder(order.getUserId(), order.getTariffId());
        orderRepository.save(inputOrder);
        return inputOrder.getOrderId();
    }

    private LoanOrder generateOrder(long user_id, long tariff_id) {
        log.info("Generating loan order");
        String orderId = UUID.randomUUID().toString();
        double credit_rating = generateRating();
        LoanOrder order = new LoanOrder(
                orderId,
                user_id,
                tariff_id,
                credit_rating,
                IN_PROGRESS_STATUS);
        return order;
    }

    public static double generateRating() {
        log.info("Generating rating");
        double rating = ThreadLocalRandom.current().nextDouble(0.1, 0.9);
        double formalized_rating = Double.parseDouble(
                String.format("%." + 2 + "f", rating)
                        .replace(",", "."));
        return formalized_rating;
    }

    public String getOrderStatus(String orderId) throws LoanOrderNotFoundException {
        try {
            log.info("Find loan order by orderId");
            return orderRepository
                    .findByOrderId(orderId)
                    .getStatus();
        } catch (LoanOrderNotFoundException ex) {
            log.error("Loan order not found by orderId");
            throw ex;
        }
    }

    public void deleteLoanOrder(DeleteOrderDto deleteOrderDto) throws LoanOrderNotFoundException, OrderImpossibleToDeleteException {
        LoanOrder loanOrder = orderRepository.findByUserIdAndOrderId(deleteOrderDto.getUserId(), deleteOrderDto.getOrderId());
        if (loanOrder.getStatus().equals(IN_PROGRESS_STATUS)) {
            log.info("Delete loan order");
            orderRepository.deleteById(loanOrder.getId());
        } else {
            log.error("Order impossible to delete");
            throw new OrderImpossibleToDeleteException();
        }
    }

    @Scheduled(fixedRate = TWO_MINUTES)
    private void considerLoanOrder() {
        log.info("Consider loan orders");
        List<LoanOrder> notConsideredOrders = orderRepository.findAllInProgress();
        notConsideredOrders.forEach(order -> {
            setOrderStatus(order);
            orderRepository.update(order);
        });
    }

    private void setOrderStatus(LoanOrder order) {
        if (isOrderApproved()) {
            order.setStatus(APPROVED_STATUS);
        } else {
            order.setStatus(REFUSED_STATUS);
        }
    }

    private boolean isOrderApproved() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
