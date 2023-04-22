package mts.fintech.creditservice.repository;

import mts.fintech.creditservice.entity.Tariff;

import java.util.List;

public interface TariffRepository {
    int save(Tariff tariff);

    int update(Tariff tariff);

    Tariff findById(Long id);

    List<Tariff> findAll();

    int deleteById(Long id);

    int deleteAll();
}
