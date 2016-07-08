package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;

/**
 * User: sennen
 * Date: 06/07/2016
 * Time: 10:42
 */
public interface StockExchangeRepository {
    SecurityWithStockExchange findSecurity(String stockExchangeSymbol, String securitySymbol);
}
