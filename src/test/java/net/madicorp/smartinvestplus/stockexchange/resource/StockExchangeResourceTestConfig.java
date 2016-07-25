package net.madicorp.smartinvestplus.stockexchange.resource;

import net.madicorp.smartinvestplus.config.JacksonConfiguration;
import net.madicorp.smartinvestplus.date.DateService;
import net.madicorp.smartinvestplus.stockexchange.repository.CloseRateRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import net.madicorp.smartinvestplus.stockexchange.service.CloseRateService;
import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
import net.madicorp.smartinvestplus.web.rest.HttpUtil;
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
    public SecuritiesResource securitiesResource() {
        return new SecuritiesResource();
    }

    @Bean
    public HttpUtil httpUtil() {
        return new HttpUtil();
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

    @Bean
    public CloseRateService closeRateService() {
        return mock(CloseRateService.class);
    }

    @Bean
    public CloseRateRepository closeRateRepository() {
        return mock(CloseRateRepository.class);
    }

    @Bean
    public DateService dateService() {
        return mock(DateService.class);
    }
}
