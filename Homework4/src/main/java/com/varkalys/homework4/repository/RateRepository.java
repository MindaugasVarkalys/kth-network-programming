package com.varkalys.homework4.repository;

import com.varkalys.homework4.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RateRepository extends JpaRepository<Rate, Integer> {

    @Query("select r from Rate r where r.fromCurrency.id = ?1 and r.toCurrency.id = ?2")
    Rate findByCurrencyIds(int fromCurrencyId, int toCurrencyId);
}
