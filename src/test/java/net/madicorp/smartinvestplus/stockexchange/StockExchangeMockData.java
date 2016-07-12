package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.stockexchange.domain.*;

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
        stockExchange.getSecurities().add(smplSecurity(1));
        stockExchange.getSecurities().add(smplSecurity(2));
        return stockExchange;
    }

    public static SecurityWithStockExchange security() {
        return security(1);
    }

    public static SecurityWithStockExchange security(int idx) {
        SecurityWithStockExchange security = new SecurityWithStockExchange();
        security.getStockExchange().setSymbol("BRVM");
        security.getStockExchange().setName("Bourse Régionale des VM");
        security.setName("Security " + idx);
        security.setSymbol("sec_" + idx);
        return security;
    }

    private static Security smplSecurity(int idx) {
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
        SecurityWithStockExchange security = security(1);
        closeRate.setSecuritySymbol(security.getSymbol());
        closeRate.setStockExchangeSymbol(security.getStockExchange().getSymbol());
        closeRate.setDate(date);
        closeRate.setRate(rate);
        closeRate.setGenerated(generated);
        return closeRate;
    }

    public static Division division(LocalDate date, double rate) {
        Division division = new Division();
        division.setDate(date);
        division.setRate(rate);
        return division;
    }
}
