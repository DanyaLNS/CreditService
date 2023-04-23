package mts.fintech.creditservice.repository.impl;

import mts.fintech.creditservice.entity.LoanOrder;
import mts.fintech.creditservice.exceptions.LoanOrderNotFoundByUserAndTariffException;
import mts.fintech.creditservice.repository.LoanOrderRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class LoanOrderRepositoryImpl implements LoanOrderRepository {
    final JdbcTemplate jdbcTemplate;

    public LoanOrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public int save(LoanOrder order) {
        return jdbcTemplate.update(
                "INSERT INTO loan_order (order_id, user_id, tariff_id, credit_rating, status) VALUES (?,?,?,?,?)",
                new Object[]{order.getOrder_id(), order.getUser_id(), order.getTariff_id(), order.getCredit_rating(), order.getStatus()}
        );
    }

    @Override
    public int update(LoanOrder order) {
        Timestamp timeUpdate = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(
                "UPDATE loan_order SET order_id=?, user_id=?, tariff_id=?, credit_rating=?, status=?, time_update=? WHERE id=?",
                new Object[]{order.getOrder_id(), order.getUser_id(), order.getTariff_id(), order.getCredit_rating(), order.getStatus(), timeUpdate, order.getId()}
        );
    }

    @Override
    public LoanOrder findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM loan_order WHERE id=?",
                    BeanPropertyRowMapper.newInstance(LoanOrder.class), id);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw ex;
        }
    }

    @Override
    public LoanOrder findByUserIdAndTariffId(Long userId, Long tariffId) throws LoanOrderNotFoundByUserAndTariffException {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM loan_order WHERE user_id=? AND tariff_id=?",
                    BeanPropertyRowMapper.newInstance(LoanOrder.class),
                    userId, tariffId);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new LoanOrderNotFoundByUserAndTariffException();
        }
    }

    @Override
    public List<LoanOrder> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM loan_order",
                BeanPropertyRowMapper.newInstance(LoanOrder.class));
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(
                "DELETE FROM loan_order WHERE id=?", id);
    }

    @Override
    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM loan_order");
    }
}
