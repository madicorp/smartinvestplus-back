package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.date.DateService;
import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import net.madicorp.smartinvestplus.stockexchange.repository.CloseRateRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
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
        SecurityWithStockExchange security = stockExchangeRepository.findSecurity(stockExchangeSymbol, securitySymbol);
        Iterator<CloseRate> existingOneMonthToDateCloseRates =
            closeRateRepository.findOneMonthToDateCloseRates(stockExchangeSymbol, securitySymbol, to);
        return new CloseRateIterator(to.minusMonths(1), to,
                                     existingOneMonthToDateCloseRates,
                                     (date) -> dateService.nextOpenDay(date),
                                     (date) -> getClosestCloseRateInPast(stockExchangeSymbol, securitySymbol, date),
                                     () -> closeRate(security));
    }

    public void saveGenerated(String stockExchangeSymbol, String securitySymbol, Iterator<CloseRate> closeRates) {
        CloseRate[] generatedCloseRates =
            StreamSupport.stream(Spliterators.spliteratorUnknownSize(closeRates, Spliterator.ORDERED), false)
                         .filter(CloseRate::isGenerated)
                         .toArray(CloseRate[]::new);
        closeRateRepository.save(stockExchangeSymbol, securitySymbol, generatedCloseRates);
    }

    private CloseRate closeRate(SecurityWithStockExchange security) {
        CloseRate closeRate = new CloseRate();
        closeRate.setSecurity(security);
        return closeRate;
    }

    private CloseRate getClosestCloseRateInPast(String stockExchangeSymbol, String securitySymbol, LocalDate date) {
        String errorMsg = String.format("No close rate found before %s", date.format(DateTimeFormatter.BASIC_ISO_DATE));
        IncompleteDataHistoryException exception = new IncompleteDataHistoryException(errorMsg);
        return closeRateRepository.findClosestCloseRateInPast(stockExchangeSymbol, securitySymbol, date)
                                  .orElseThrow(() -> exception);
    }
}
