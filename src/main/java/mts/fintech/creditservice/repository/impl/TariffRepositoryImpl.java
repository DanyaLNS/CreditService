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
    private final JdbcTemplate jdbcTemplate;

    public TariffRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Tariff tariff) {
        return jdbcTemplate.update(
                "INSERT INTO tariff (type, interest_rate) VALUES(?,?)",
                new Object[]{tariff.getType(), tariff.getInterest_rate()});
    }

    @Override
    public int update(Tariff tariff) {
        return jdbcTemplate.update(
                "UPDATE tariff SET type=?, interest_rate=? WHERE id=?",
                new Object[]{tariff.getType(), tariff.getInterest_rate(), tariff.getId()});
    }

    @Override
    public Tariff findById(Long id) throws TariffNotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM tariff WHERE id=?",
                    BeanPropertyRowMapper.newInstance(Tariff.class), id);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new TariffNotFoundException();
        }
    }

    @Override
    public List<Tariff> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM tariff",
                BeanPropertyRowMapper.newInstance(Tariff.class));
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(
                "DELETE FROM tariff WHERE id=?", id);
    }

    @Override
    public int deleteAll() {
        return jdbcTemplate.update(
                "DELETE FROM tariff");
    }
}
