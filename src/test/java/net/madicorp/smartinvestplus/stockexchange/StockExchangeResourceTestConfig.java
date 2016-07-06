package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.config.JacksonConfiguration;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import static org.mockito.Mockito.mock;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 15:39
 */
@Configuration
@Import(JacksonConfiguration.class)
public class StockExchangeResourceTestConfig {

    @Bean
    public StockExchangeResource stockExchangeResource() {
        return new StockExchangeResource();
    }

    @Bean
    public SecuritiesResource securitiesResource() {
        return new SecuritiesResource();
    }

    @Bean
    public StockExchangeCRUDRepository stockExchangeCRUDRepository() {
        return mock(StockExchangeCRUDRepository.class);
    }

    @Bean
    public StockExchangeRepository stockExchangeRepository() {
        return mock(StockExchangeRepository.class);
    }

    @Bean
    public StockExchangeService stockExchangeService() {
        return new StockExchangeService();
    }
}
