package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import net.madicorp.smartinvestplus.stockexchange.domain.StockExchangeWithSecurities;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 19:32
 */
@RunWith(MockitoJUnitRunner.class)
public class StockExchangeServiceTest {
    @Mock
    private StockExchangeCRUDRepository crudRepository;

    @Mock
    private StockExchangeRepository repository;

    @InjectMocks
    private StockExchangeService subject;

    @Test
    public void should_retrieve_stock_exchange_if_crud_repo_returns_one() throws Exception {
        // GIVEN
        String stockExchangeSymbol = "BRVM";
        StockExchangeWithSecurities dummyStockExchange = new StockExchangeWithSecurities();
        Mockito.when(crudRepository.findOne(stockExchangeSymbol)).thenReturn(dummyStockExchange);

        // WHEN
        Optional<StockExchangeWithSecurities> actual = subject.getStockExchange(stockExchangeSymbol);

        // THEN
        Assertions.assertThat(actual).contains(dummyStockExchange);
    }

    @Test
    public void should_return_empty_if_crud_repo_returns_no_stock_exchange() throws Exception {
        // GIVEN
        String stockExchangeSymbol = "BRVM";
        StockExchangeWithSecurities dummyStockExchange = new StockExchangeWithSecurities();
        Mockito.when(crudRepository.findOne(stockExchangeSymbol)).thenReturn(null);

        // WHEN
        Optional<StockExchangeWithSecurities> actual = subject.getStockExchange(stockExchangeSymbol);

        // THEN
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    public void should_retrieve_security_if_repo_returns_one() throws Exception {
        // GIVEN
        String stockExchangeSymbol = "BRVM";
        String securitySymbol = "sec_1";
        SecurityWithStockExchange dummySecurity = new SecurityWithStockExchange();
        Mockito.when(repository.findSecurity(stockExchangeSymbol, securitySymbol)).thenReturn(dummySecurity);

        // WHEN
        Optional<SecurityWithStockExchange> actual = subject.getSecurity(stockExchangeSymbol, securitySymbol);

        // THEN
        Assertions.assertThat(actual).contains(dummySecurity);
    }

    @Test
    public void should_return_empty_if_repo_returns_no_security() throws Exception {
        // GIVEN
        String stockExchangeSymbol = "BRVM";
        String securitySymbol = "sec_1";
        Mockito.when(repository.findSecurity(stockExchangeSymbol, securitySymbol)).thenReturn(null);

        // WHEN
        Optional<SecurityWithStockExchange> actual = subject.getSecurity(stockExchangeSymbol, securitySymbol);

        // THEN
        Assertions.assertThat(actual).isEmpty();
    }
}
