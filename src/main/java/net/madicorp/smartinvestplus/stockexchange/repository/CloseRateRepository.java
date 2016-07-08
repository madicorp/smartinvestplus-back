package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

/**
 * User: sennen
 * Date: 06/07/2016
 * Time: 10:42
 */
public interface CloseRateRepository {
    Iterator<CloseRate> findOneMonthToDateCloseRates(String stockExchangeSymbol, String securitySymbol, LocalDate to);
    Optional<CloseRate> findClosestCloseRateInPast(String stockExchangeSymbol, String securitySymbol, LocalDate date);

    void save(String stockExchangeSymbol, String securitySymbol, CloseRate[] generatedCloseRates);
}
