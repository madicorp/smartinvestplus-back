package net.madicorp.smartinvestplus.date;

import net.madicorp.smartinvestplus.config.JacksonConfiguration;
import net.madicorp.smartinvestplus.stockexchange.repository.CloseRateRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
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
public class DateResourceTestConfig {

    @Bean
    public StockExchangeService stockExchangeService() {
        return new StockExchangeService();
    }

    @Bean
    public StockExchangeCRUDRepository stockExchangeCRUDRepository() {
        return mock(StockExchangeCRUDRepository.class);
    }

    @Bean
    public StockExchangeRepository stockExchangeRepository() {
        return mock(StockExchangeRepository.class);
    }

}
