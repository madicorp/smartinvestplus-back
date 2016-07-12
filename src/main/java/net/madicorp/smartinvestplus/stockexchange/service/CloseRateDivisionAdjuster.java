package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.Division;

import java.time.LocalDate;
import java.util.TreeMap;

/**
 * User: sennen
 * Date: 11/07/2016
 * Time: 19:49
 */
public class CloseRateDivisionAdjuster implements CloseRateAdjuster {
    private final TreeMap<LocalDate, Division> divisions;

    public CloseRateDivisionAdjuster(TreeMap<LocalDate, Division> divisions) {
        this.divisions = divisions;
    }

    public CloseRate adjust(CloseRate closeRate) {
        CloseRate adjustedCloseRate = new CloseRate();
        LocalDate closeDate = closeRate.getDate();
        adjustedCloseRate.setDate(closeDate);
        adjustedCloseRate.setStockExchangeSymbol(closeRate.getStockExchangeSymbol());
        adjustedCloseRate.setSecuritySymbol(closeRate.getSecuritySymbol());
        adjustedCloseRate.setGenerated(closeRate.isGenerated());
        adjustedCloseRate.setRate(closeRate.getRate());
        divisions.headMap(closeDate)
                 .values()
                 .stream()
                 .map(Division::getRate)
                 .forEach(rate -> {
                     System.out.println(adjustedCloseRate.getRate() + " : " + rate);
                     adjustedCloseRate.setRate(adjustedCloseRate.getRate() * rate);
                 });
        return adjustedCloseRate;
    }
}
