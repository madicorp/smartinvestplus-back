package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.date.StockExchangeHoliday;
import net.madicorp.smartinvestplus.stockexchange.domain.Division;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import net.madicorp.smartinvestplus.stockexchange.domain.StockExchangeWithSecurities;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

/**
 * User: sennen
 * Date: 04/07/2016
 * Time: 23:39
 */
@Service
public class StockExchangeService {
    @Inject
    private StockExchangeCRUDRepository crudRepository;

    @Inject
    private StockExchangeRepository repository;

    public Optional<StockExchangeWithSecurities> getStockExchange(String symbol) {
        StockExchangeWithSecurities stockExchange = crudRepository.findOne(symbol);
        return optional(stockExchange);
    }

    public Optional<SecurityWithStockExchange> getSecurity(String stockExchangeSymbol, String securitySymbol) {
        SecurityWithStockExchange security = repository.findSecurity(stockExchangeSymbol, securitySymbol);
        return optional(security);
    }

    private static <T> Optional<T> optional(T value) {
        return value != null ? Optional.of(value) : Optional.empty();
    }

    public Division addDivision(SecurityWithStockExchange security, Division division) {
        if (security.getDivisions().contains(division)) {
            throw new DivisionAlreadyExistsException();
        }
        security.addDivision(division);
        repository.addDivision(security.getStockExchange().getSymbol(), security.getSymbol(), division);
        return division;
    }

    public Optional<StockExchangeHoliday> addHoliday(String stockExchangeSymbol, LocalDate holiday) {
        if (repository.getHolidays(stockExchangeSymbol).contains(holiday)) {
            return Optional.empty();
        }
        repository.addHoliday(stockExchangeSymbol, holiday);
        StockExchangeHoliday stockExchangeHoliday = StockExchangeHoliday.builder()
                                                                        .stockExchangeSymbol(stockExchangeSymbol)
                                                                        .date(holiday)
                                                                        .build();
        return Optional.of(stockExchangeHoliday);
    }

    public Optional<StockExchangeHoliday> getHoliday(String stockExchangeSymbol, LocalDate holiday) {
        StockExchangeHoliday stockExchangeHoliday = StockExchangeHoliday.builder()
                                                                        .date(holiday)
                                                                        .stockExchangeSymbol(stockExchangeSymbol)
                                                                        .build();
        return repository.containsHoliday(stockExchangeSymbol, holiday) ?
            Optional.of(stockExchangeHoliday) :
            Optional.empty();
    }

    public Optional<Division> getDivision(String stockExchangeSymbol, String securitySymbol,
                                          LocalDate divisionDate) {
        return repository.getDivision(stockExchangeSymbol, securitySymbol, divisionDate);
    }
}
