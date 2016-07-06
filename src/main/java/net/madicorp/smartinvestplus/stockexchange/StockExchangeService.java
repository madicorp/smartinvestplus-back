package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
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
}
