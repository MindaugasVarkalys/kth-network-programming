package com.varkalys.homework4.controller;

import com.varkalys.homework4.entity.Rate;
import com.varkalys.homework4.repository.CurrencyRepository;
import com.varkalys.homework4.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.MathContext;

@Controller("mainController")
@RequestMapping("/")
public class MainController {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;

    @Autowired
    public MainController(CurrencyRepository currencyRepository, RateRepository rateRepository) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
    }

    @GetMapping("/")
    @Transactional
    public String openMainPage(Model model) {
        model.addAttribute("currencies", currencyRepository.findAll());
        return "mainPage.html";
    }

    @PostMapping("/")
    @Transactional
    public String convert(@RequestParam("fromCurrencyId") int fromCurrencyId, @RequestParam("toCurrencyId") int toCurrencyId, @RequestParam("value") String value, Model model) {
        model.addAttribute("currencies", currencyRepository.findAll());
        model.addAttribute("fromCurrencyId", fromCurrencyId);
        model.addAttribute("toCurrencyId", toCurrencyId);
        model.addAttribute("fromValue", value);
        try {
            model.addAttribute("toValue", calculate(fromCurrencyId, toCurrencyId, value));
        } catch (CalculationException ex) {
            model.addAttribute("error", ex.getLocalizedMessage());
        }
        return "mainPage.html";
    }

    private BigDecimal calculate(int fromCurrencyId, int toCurrencyId, String fromValueStr) throws CalculationException {
        if (fromValueStr.isEmpty()) {
            throw new CalculationException("Value cannot be empty");
        }
        BigDecimal fromValue = new BigDecimal(fromValueStr);
        if (fromValue.doubleValue() < 0) {
            throw new CalculationException("Value cannot be less than zero");
        }
        if (fromCurrencyId == toCurrencyId) {
            return fromValue;
        }
        Rate rate = rateRepository.findByCurrencyIds(fromCurrencyId, toCurrencyId);
        if (rate == null) {
            rate = rateRepository.findByCurrencyIds(toCurrencyId, fromCurrencyId);
            rate.setRate(BigDecimal.ONE.divide(rate.getRate(), MathContext.DECIMAL128));
        }
        return fromValue.multiply(rate.getRate());
    }

    private class CalculationException extends Exception {
        public CalculationException(String message) {
            super(message);
        }
    }
}
