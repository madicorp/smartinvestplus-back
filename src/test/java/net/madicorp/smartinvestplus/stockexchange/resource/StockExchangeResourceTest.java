package net.madicorp.smartinvestplus.stockexchange.resource;

import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.test.HttpTestConfig;
import net.madicorp.smartinvestplus.test.HttpTestInjectBean;
import net.madicorp.smartinvestplus.test.HttpTestRule;
import net.madicorp.smartinvestplus.test.ResponseAssertion;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Collections;

import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.stockExchange;
import static org.mockito.Mockito.when;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 15:39
 */
@HttpTestConfig(StockExchangeResourceTestConfig.class)
public class StockExchangeResourceTest {
    @ClassRule
    public static final HttpTestRule rule = new HttpTestRule();

    @HttpTestInjectBean
    private static StockExchangeCRUDRepository mockRepo;

    @Test
    public void should_return_stock_exchanges_and_related_security_links() throws Exception {
        // GIVEN
        when(mockRepo.findAll()).thenReturn(Collections.singletonList(stockExchange()));

        // WHEN
        Response actual = rule.target("/api/stock-exchanges/").request().get();

        // THEN
        ResponseAssertion.assertThat(actual)
                         .success()
                         .hasSize("$", 1)
                         .contains("$[0].symbol", "BRVM")
                         .contains("$[0].links[0].rel", "security")
                         .contains("$[0].links[0].href", "/api/stock-exchanges/BRVM/securities/sec_1");
    }

    @Test
    public void should_return_stock_exchange_and_related_security_links() throws Exception {
        // GIVEN
        when(mockRepo.findOne("BRVM")).thenReturn(stockExchange());

        // WHEN
        Response actual = rule.target("/api/stock-exchanges/BRVM").request().get();

        // THEN
        ResponseAssertion.assertThat(actual)
                         .success()
                         .hasSize("$.links", 2)
                         .contains("$.symbol", "BRVM")
                         .contains("$.links[0].rel", "security")
                         .contains("$.links[0].href", "/api/stock-exchanges/BRVM/securities/sec_1");
    }

    @Test
    public void should_return_404_if_stock_exchange_not_found() throws Exception {
        // GIVEN
        when(mockRepo.findOne("BRVM")).thenReturn(null);

        // WHEN
        Response actual = rule.target("/api/stock-exchanges/BRVM").request().get();

        // THEN
        ResponseAssertion.assertThat(actual)
                         .notFound()
                         .payloadIsEqualTo("Stock exchange 'BRVM' has not been found");
    }

}
