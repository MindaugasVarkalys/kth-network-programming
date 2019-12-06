package com.varkalys.homework4.service;

import com.varkalys.homework4.entity.Currency;
import com.varkalys.homework4.entity.Rate;
import com.varkalys.homework4.repository.CurrencyRepository;
import com.varkalys.homework4.repository.RateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SeedService {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;

    public SeedService(CurrencyRepository currencyRepository, RateRepository rateRepository) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
    }

    public void seed() {
        if (needsSeeding()) {
            addCurrenciesAndRates();
        }
    }


    private boolean needsSeeding() {
        return currencyRepository.count() == 0;
    }

    private void addCurrenciesAndRates() {
        Currency eur = addCurrency("EUR");
        Currency sek = addCurrency("SEK");
        Currency usd = addCurrency("USD");
        Currency gbp = addCurrency("GBP");
        currencyRepository.flush();

        addRate(eur, sek, "10.5135");
        addRate(eur, usd, "1.10763");
        addRate(eur, gbp, "0.843680");
        addRate(sek, usd, "0.105452");
        addRate(sek, gbp, "0.0803119");
        addRate(usd, gbp, "0.761941");
        rateRepository.flush();
    }

    private Currency addCurrency(String name) {
        Currency currency = new Currency();
        currency.setName(name);
        currencyRepository.save(currency);
        return currency;
    }

    private void addRate(Currency fromCurrency, Currency toCurrency, String rate) {
        Rate dbRate = new Rate();
        dbRate.setFromCurrency(fromCurrency);
        dbRate.setToCurrency(toCurrency);
        dbRate.setRate(new BigDecimal(rate));
        rateRepository.save(dbRate);
    }
}
