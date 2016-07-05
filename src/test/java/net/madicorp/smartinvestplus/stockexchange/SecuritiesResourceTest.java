package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.test.HttpTestHelperBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Collections;

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
    private static StockExchangeRepository mockRepo;

    @BeforeClass
    public static void initContext() throws Exception {
        helper = builder(() -> StockExchangeResourceTestConfig.class).build();
        mockRepo = helper.context().getBean(StockExchangeRepository.class);
    }

    @AfterClass
    public static void closeHelper() throws Exception {
        helper.tearDown();
    }

    @Test
    public void should_return_brvm_securities() throws Exception {
        // GIVEN
        when(mockRepo.findOne("BRVM")).thenReturn(stockExchange());

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

}
