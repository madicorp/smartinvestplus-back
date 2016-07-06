package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import net.madicorp.smartinvestplus.test.HttpTestHelperBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.security;
import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.stockExchange;
import static net.madicorp.smartinvestplus.test.HttpTestHelperBuilder.builder;
import static org.mockito.Mockito.when;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 21:46
 */
public class SecuritiesResourceTest {
    private static HttpTestHelperBuilder.HttpTestHelper helper;
    private static StockExchangeCRUDRepository mockCRUDRepo;
    private static StockExchangeRepository mockRepo;

    @BeforeClass
    public static void initContext() throws Exception {
        helper = builder(() -> StockExchangeResourceTestConfig.class).build();
        mockCRUDRepo = helper.context().getBean(StockExchangeCRUDRepository.class);
        mockRepo = helper.context().getBean(StockExchangeRepository.class);
    }

    @AfterClass
    public static void closeHelper() throws Exception {
        helper.tearDown();
    }

    @Test
    public void should_return_brvm_securities() throws Exception {
        // GIVEN
        when(mockCRUDRepo.findOne("BRVM")).thenReturn(stockExchange());

        // WHEN
        Response actual = helper.target("/api/stock-exchanges/BRVM/securities").request().get();

        // THEN
        helper.assertThat(actual)
              .success()
              .hasSize("$", 2)
              .contains("$[0].symbol", "sec_1")
              .contains("$[0].name", "Security 1")
              .contains("$[0].link", "/api/stock-exchanges/BRVM/securities/sec_1");
    }

    @Test
    public void should_return_sec_1_security() throws Exception {
        // GIVEN
        when(mockRepo.findSecurity("BRVM", "sec_1")).thenReturn(security());

        // WHEN
        Response actual = helper.target("/api/stock-exchanges/BRVM/securities/sec_1").request().get();

        // THEN
        helper.assertThat(actual)
              .success()
              .contains("$.stockExchange.symbol", "BRVM")
              .contains("$.stockExchange.name", "Bourse Régionale des VM")
              .contains("$.symbol", "sec_1")
              .contains("$.name", "Security 1");
    }

}
