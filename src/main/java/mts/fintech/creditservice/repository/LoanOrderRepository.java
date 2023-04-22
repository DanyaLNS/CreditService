package mts.fintech.creditservice.repository;

import mts.fintech.creditservice.entity.LoanOrder;

import java.util.List;

public interface LoanOrderRepository {
    int save(LoanOrder order);

    int update(LoanOrder order);

    LoanOrder findById(Long id);

    List<LoanOrder> findAll();

    int deleteById(Long id);

    int deleteAll();
}
