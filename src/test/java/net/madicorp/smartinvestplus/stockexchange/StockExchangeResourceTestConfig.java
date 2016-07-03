package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.config.JacksonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
    public StockExchangeRepository stockExchangeRepository() {
        return mock(StockExchangeRepository.class);
    }
}
