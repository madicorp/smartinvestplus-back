package net.madicorp.smartinvestplus.stockexchange.service;

import com.google.common.collect.Maps;
import net.madicorp.smartinvestplus.date.DateService;
import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.Division;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import net.madicorp.smartinvestplus.stockexchange.repository.CloseRateRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * User: sennen
 * Date: 07/07/2016
 * Time: 22:33
 */
@Service
public class CloseRateService {
    @Inject
    private StockExchangeRepository stockExchangeRepository;

    @Inject
    private CloseRateRepository closeRateRepository;

    @Inject
    private DateService dateService;

    public Iterator<CloseRate> getOneMonthToDateCloseRates(String stockExchangeSymbol, String securitySymbol,
                                                           LocalDate to) {
        TreeMap<LocalDate, Division> sortedDivisions = getSortedDivisions(stockExchangeSymbol, securitySymbol, to);
        Iterator<CloseRate> existingOneMonthToDateCloseRates =
            closeRateRepository.findOneMonthToDateCloseRates(stockExchangeSymbol, securitySymbol, to);
        Set<LocalDate> holidays = stockExchangeRepository.getHolidays(stockExchangeSymbol);
        return new CloseRateIterator(to.minusMonths(1), to,
                                     existingOneMonthToDateCloseRates,
                                     (date) -> dateService.nextOpenDay(date, holidays),
                                     (date) -> getClosestCloseRateInPast(stockExchangeSymbol, securitySymbol, date),
                                     () -> closeRate(stockExchangeSymbol, securitySymbol),
                                     new CloseRateDivisionAdjuster(sortedDivisions));
    }

    private TreeMap<LocalDate, Division> getSortedDivisions(String stockExchangeSymbol, String securitySymbol,
                                                            LocalDate to) {
        TreeMap<LocalDate, Division> sortedDivisions = Maps.newTreeMap();
        for (Division division : stockExchangeRepository.getDivisions(stockExchangeSymbol, securitySymbol, to)) {
            sortedDivisions.put(division.getDate(), division);
        }
        return sortedDivisions;
    }

    public void saveGenerated(Iterator<CloseRate> closeRates) {
        CloseRate[] generatedCloseRates =
            StreamSupport.stream(Spliterators.spliteratorUnknownSize(closeRates, Spliterator.ORDERED), false)
                         .filter(CloseRate::isGenerated)
                         .toArray(CloseRate[]::new);
        closeRateRepository.save(generatedCloseRates);
    }

    private CloseRate closeRate(String stockExchangeSymbol, String securitySymbol) {
        CloseRate closeRate = new CloseRate();
        closeRate.setStockExchangeSymbol(stockExchangeSymbol);
        closeRate.setSecuritySymbol(securitySymbol);
        return closeRate;
    }

    private CloseRate getClosestCloseRateInPast(String stockExchangeSymbol, String securitySymbol, LocalDate date) {
        String errorMsg = String.format("No close rate found before %s", date.format(DateTimeFormatter.BASIC_ISO_DATE));
        IncompleteDataHistoryException exception = new IncompleteDataHistoryException(errorMsg);
        return closeRateRepository.findClosestCloseRateInPast(stockExchangeSymbol, securitySymbol, date)
                                  .orElseThrow(() -> exception);
    }
}
