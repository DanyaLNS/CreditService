package mts.fintech.creditservice.repository.impl;

import mts.fintech.creditservice.entity.LoanOrder;
import mts.fintech.creditservice.exceptions.LoanOrderNotFoundException;
import mts.fintech.creditservice.repository.LoanOrderRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class LoanOrderRepositoryImpl implements LoanOrderRepository {
    private static final String SAVE_SQL = "INSERT INTO loan_order (order_id, user_id, tariff_id, credit_rating, status) VALUES (?,?,?,?,?)";
    private static final String UPDATE_SQL = "UPDATE loan_order SET order_id=?, user_id=?, tariff_id=?, credit_rating=?, status=?, time_update=? WHERE id=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM loan_order WHERE id=?";
    private static final String FIND_BY_USER_AND_TARIFF_SQL = "SELECT * FROM loan_order WHERE user_id=? AND tariff_id=?";
    private static final String FIND_BY_ORDER_SQL = "SELECT * FROM loan_order WHERE order_id=?";
    private static final String FIND_BY_USER_AND_ORDER_SQL = "SELECT * FROM loan_order WHERE user_id=? AND order_id=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM loan_order";
    private static final String FIND_ALL_IN_PROGRESS_SQL = "SELECT * FROM loan_order WHERE status='IN_PROGRESS'";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM loan_order WHERE id=?";
    private static final String DELETE_ALL_SQL = "DELETE FROM loan_order";

    final JdbcTemplate jdbcTemplate;

    public LoanOrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public int save(LoanOrder order) {
        return jdbcTemplate.update(
                SAVE_SQL,
                new Object[]{order.getOrderId(), order.getUserId(), order.getTariffId(), order.getCreditRating(), order.getStatus()}
        );
    }

    @Override
    public int update(LoanOrder order) {
        Timestamp timeUpdate = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(
                UPDATE_SQL,
                new Object[]{order.getOrderId(), order.getUserId(), order.getTariffId(), order.getCreditRating(), order.getStatus(), timeUpdate, order.getId()}
        );
    }

    @Override
    public LoanOrder findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_BY_ID_SQL,
                    BeanPropertyRowMapper.newInstance(LoanOrder.class), id);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw ex;
        }
    }

    @Override
    public LoanOrder findByUserIdAndTariffId(Long userId, Long tariffId) throws LoanOrderNotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_BY_USER_AND_TARIFF_SQL,
                    BeanPropertyRowMapper.newInstance(LoanOrder.class),
                    userId, tariffId);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new LoanOrderNotFoundException();
        }
    }

    @Override
    public LoanOrder findByOrderId(String orderId) throws LoanOrderNotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_BY_ORDER_SQL,
                    BeanPropertyRowMapper.newInstance(LoanOrder.class),
                    orderId);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new LoanOrderNotFoundException();
        }
    }

    @Override
    public LoanOrder findByUserIdAndOrderId(long userId, String orderId) throws LoanOrderNotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_BY_USER_AND_ORDER_SQL,
                    BeanPropertyRowMapper.newInstance(LoanOrder.class),
                    userId, orderId);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new LoanOrderNotFoundException("ORDER_NOT_FOUND");
        }
    }

    @Override
    public List<LoanOrder> findAll() {
        return jdbcTemplate.query(
                FIND_ALL_SQL,
                BeanPropertyRowMapper.newInstance(LoanOrder.class));
    }

    @Override
    public List<LoanOrder> findAllInProgress() {
        return jdbcTemplate.query(
                FIND_ALL_IN_PROGRESS_SQL,
                BeanPropertyRowMapper.newInstance(LoanOrder.class));
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(
                DELETE_BY_ID_SQL, id);
    }

    @Override
    public int deleteAll() {
        return jdbcTemplate.update(DELETE_ALL_SQL);
    }
}
