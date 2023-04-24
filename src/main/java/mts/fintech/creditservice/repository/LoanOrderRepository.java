package mts.fintech.creditservice.repository;

import mts.fintech.creditservice.entity.LoanOrder;
import mts.fintech.creditservice.exceptions.LoanOrderNotFoundException;

import java.util.List;

public interface LoanOrderRepository {
    int save(LoanOrder order);

    int update(LoanOrder order);

    LoanOrder findById(Long id);

    LoanOrder findByUserIdAndTariffId(Long userId, Long tariffId) throws LoanOrderNotFoundException;

    LoanOrder findByOrderId(String orderId) throws LoanOrderNotFoundException;
    LoanOrder findByUserIdAndOrderId(long userId, String orderId) throws LoanOrderNotFoundException;

    List<LoanOrder> findAll();

    int deleteById(Long id);

    int deleteAll();

    List<LoanOrder> findAllInProgress();
}
