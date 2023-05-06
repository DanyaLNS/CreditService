package mts.fintech.creditservice.repository.impl;

import mts.fintech.creditservice.entity.Tariff;
import mts.fintech.creditservice.exceptions.TariffNotFoundException;
import mts.fintech.creditservice.repository.TariffRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TariffRepositoryImpl implements TariffRepository {
    private static final String SAVE_SQL = "INSERT INTO tariff (type, interest_rate) VALUES(?,?)";
    private static final String UPDATE_SQL = "UPDATE tariff SET type=?, interest_rate=? WHERE id=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM tariff WHERE id=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM tariff";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM tariff WHERE id=?";
    private static final String DELETE_ALL_SQL = "DELETE FROM tariff";
    private final JdbcTemplate jdbcTemplate;

    public TariffRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Tariff tariff) {
        return jdbcTemplate.update(
                SAVE_SQL,
                new Object[]{tariff.getType(), tariff.getInterest_rate()});
    }

    @Override
    public int update(Tariff tariff) {
        return jdbcTemplate.update(
                UPDATE_SQL,
                new Object[]{tariff.getType(), tariff.getInterest_rate(), tariff.getId()});
    }

    @Override
    public Tariff findById(Long id) throws TariffNotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_BY_ID_SQL,
                    BeanPropertyRowMapper.newInstance(Tariff.class), id);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new TariffNotFoundException();
        }
    }

    @Override
    public List<Tariff> findAll() {
        return jdbcTemplate.query(
                FIND_ALL_SQL,
                BeanPropertyRowMapper.newInstance(Tariff.class));
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
