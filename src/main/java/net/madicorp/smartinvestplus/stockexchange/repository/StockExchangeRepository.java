package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.stockexchange.domain.Division;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
 * User: sennen
 * Date: 06/07/2016
 * Time: 10:42
 */
public interface StockExchangeRepository {
    SecurityWithStockExchange findSecurity(String stockExchangeSymbol, String securitySymbol);

    void addDivision(String stockExchangeSymbol, String securitySymbol, Division division);

    Iterable<Division> getDivisions(String stockExchangeSymbol, String securitySymbol, LocalDate to);

    void addHoliday(String stockExchangeSymbol, LocalDate holiday);

    Set<LocalDate> getHolidays(String stockExchangeSymbol);

    boolean containsHoliday(String stockExchangeSymbol, LocalDate holiday);

    Optional<Division> getDivision(String stockExchangeSymbol, String securitySymbol, LocalDate divisionDate);
}
