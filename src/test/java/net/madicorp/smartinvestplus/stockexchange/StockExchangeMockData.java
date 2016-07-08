package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.Security;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import net.madicorp.smartinvestplus.stockexchange.domain.StockExchangeWithSecurities;

import java.time.LocalDate;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 22:51
 */
public class StockExchangeMockData {
    public static StockExchangeWithSecurities stockExchange() {
        StockExchangeWithSecurities stockExchange = new StockExchangeWithSecurities();
        stockExchange.setSymbol("BRVM");
        stockExchange.setName("Bourse Régionale des VM");
        stockExchange.getSecurities().add(security(1));
        stockExchange.getSecurities().add(security(2));
        return stockExchange;
    }

    public static SecurityWithStockExchange security() {
        SecurityWithStockExchange security = new SecurityWithStockExchange();
        security.getStockExchange().setSymbol("BRVM");
        security.getStockExchange().setName("Bourse Régionale des VM");
        security.setName("Security 1");
        security.setSymbol("sec_1");
        return security;
    }

    private static Security security(int idx) {
        Security security = new Security();
        security.setName("Security " + idx);
        security.setSymbol("sec_" + idx);
        return security;
    }

    public static CloseRate closeRate(LocalDate date, double rate) {
        return closeRate(date, rate, false);
    }

    public static CloseRate closeRate(LocalDate date, double rate, boolean generated) {
        CloseRate closeRate = new CloseRate();
        closeRate.setDate(date);
        closeRate.setRate(rate);
        closeRate.setGenerated(generated);
        return closeRate;
    }
}
