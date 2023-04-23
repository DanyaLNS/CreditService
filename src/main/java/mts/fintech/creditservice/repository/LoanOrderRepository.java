package mts.fintech.creditservice.repository;

import mts.fintech.creditservice.entity.LoanOrder;
import mts.fintech.creditservice.exceptions.LoanOrderNotFoundByUserAndTariffException;

import java.util.List;

public interface LoanOrderRepository {
    int save(LoanOrder order);

    int update(LoanOrder order);

    LoanOrder findById(Long id);

    LoanOrder findByUserIdAndTariffId(Long userId, Long tariffId) throws LoanOrderNotFoundByUserAndTariffException;

    List<LoanOrder> findAll();

    int deleteById(Long id);

    int deleteAll();

}
