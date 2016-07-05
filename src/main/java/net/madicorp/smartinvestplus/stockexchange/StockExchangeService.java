package net.madicorp.smartinvestplus.stockexchange;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

/**
 * User: sennen
 * Date: 04/07/2016
 * Time: 23:39
 */
@Service
public class StockExchangeService {
    @Inject
    private StockExchangeRepository repository;

    public Optional<StockExchangeWithSecurities> getStockExchange(String symbol) {
        StockExchangeWithSecurities stockExchange = repository.findOne(symbol);
        return stockExchange != null ? Optional.of(stockExchange) : Optional.empty();
    }
}
