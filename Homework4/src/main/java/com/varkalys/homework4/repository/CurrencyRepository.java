package com.varkalys.homework4.repository;

import com.varkalys.homework4.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}
