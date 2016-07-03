package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.JsonPathAssertion;
import net.madicorp.smartinvestplus.config.JerseyConfig;
import net.madicorp.smartinvestplus.config.JerseyMapperProvider;
import org.assertj.core.api.Assertions;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.mockito.Mockito.when;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 15:39
 */

public class StockExchangeResourceTest extends JerseyTest {

    private ApplicationContext context;
    private StockExchangeRepository mockRepo;

    @Override
    protected Application configure() {
        context = new AnnotationConfigApplicationContext(StockExchangeResourceTestConfig.class);
        return new JerseyConfig()
            .property(ServerProperties.PROVIDER_CLASSNAMES, JerseyMapperProvider.class.getCanonicalName())
            .property("contextConfig", context);
    }

    @Before
    public void initMockRepo() throws Exception {
        mockRepo = context.getBeansOfType(StockExchangeRepository.class).values().iterator().next();
    }

    @Test
    public void should_return_stock_exchange_and_security_links() throws Exception {
        // GIVEN
        when(mockRepo.findAll()).thenReturn(Collections.singletonList(stockExchange()));

        // WHEN
        Response stockExchangesResp = target("/api/stock-exchanges/").request()
                                                                     .get();

        // THEN
        Assertions.assertThat(stockExchangesResp.getStatus()).isEqualTo(200);
        String json = stockExchangesResp.readEntity(String.class);
        JsonPathAssertion.assertThat(json)
                         .hasSize("$", 1)
                         .contains("$[0].symbol", "BRVM")
                         .contains("$[0].links[0]", "/api/stock-exchanges/BRVM/securities/sec_1");
    }

    private StockExchangeWithSecurities stockExchange() {
        StockExchangeWithSecurities stockExchange = new StockExchangeWithSecurities();
        stockExchange.setSymbol("BRVM");
        stockExchange.setName("Bourse Régionale des VM");
        stockExchange.getSecurities().add(security(1));
        stockExchange.getSecurities().add(security(2));
        return stockExchange;
    }

    private Security security(int idx) {
        Security security = new Security();
        security.setSymbol("sec_" + idx);
        return security;
    }

}
