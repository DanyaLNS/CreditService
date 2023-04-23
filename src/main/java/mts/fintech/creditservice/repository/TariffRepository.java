package mts.fintech.creditservice.repository;

import mts.fintech.creditservice.entity.Tariff;
import mts.fintech.creditservice.exceptions.TariffNotFoundException;

import java.util.List;

public interface TariffRepository {
    int save(Tariff tariff);

    int update(Tariff tariff);

    Tariff findById(Long id) throws TariffNotFoundException;

    List<Tariff> findAll();

    int deleteById(Long id);

    int deleteAll();
}
